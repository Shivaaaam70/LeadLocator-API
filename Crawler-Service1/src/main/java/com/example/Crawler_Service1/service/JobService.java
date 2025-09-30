package com.example.Crawler_Service1.service;

import com.example.Crawler_Service1.entity.Jobs;
import com.example.Crawler_Service1.repository.JobsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JobService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final JobsRepository jobsRepository;
    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(6);

    public JobService(RestTemplate restTemplate, ObjectMapper objectMapper, JobsRepository jobsRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.jobsRepository = jobsRepository;
    }

    // ========== Public APIs ==========

    public List<Jobs> getAllJobsFromDb() {
        return jobsRepository.findAllJobs();
    }

    public List<Jobs> getJobsByKeyword(String keyword) {
        return jobsRepository.findByPositionContainingIgnoreCase(keyword);
    }

    /** Fetch all jobs and update DB */
    public void fetchAndStoreAllJobs() {
        try {
            List<Callable<List<Jobs>>> tasks = List.of(
                    this::fetchFromRemoteOk,
                    this::fetchFromArbeitnow,
                    this::fetchFromAdzuna,
                    this::fetchFromRemotive,
                    this::fetchFromUsaJobs,
                    this::fetchFromWeWorkRemotely,
                    this::fetchFromLinkedIn
            );

            List<Jobs> allJobs = executor.invokeAll(tasks, 20, TimeUnit.SECONDS).stream()
                    .flatMap(future -> {
                        try {
                            return future.get().stream();
                        } catch (Exception e) {
                            logger.error("Error fetching jobs: {}", e.getMessage());
                            return StreamSupport.stream(Collections.<Jobs>emptyList().spliterator(), false);
                        }
                    })
                    .collect(Collectors.toList());

            logger.info("Total jobs fetched: {}", allJobs.size());

            upsertJobs(allJobs);

        } catch (Exception e) {
            logger.error("Error fetching and storing jobs", e);
        }
    }

    /** Cleanup: remove jobs older than 15 days */
    @Scheduled(cron = "0 0 2 * * ?") // run daily at 2 AM
    public void removeOldJobs() {
        LocalDate cutoff = LocalDate.now().minusDays(15);

        long before = jobsRepository.count();

        // delete jobs older than 15 days
        int deletedOld = jobsRepository.deleteOldJobs(cutoff.toString());

        // delete jobs that are inactive
        int deletedInactive = jobsRepository.deleteInactiveJobs();

        long after = jobsRepository.count();

        logger.info("Deleted {} old jobs (older than {})", deletedOld, cutoff);
        logger.info("Deleted {} inactive jobs", deletedInactive);
        logger.info("Cleanup complete: before={}, after={}, totalDeleted={}", before, after, (before - after));
    }


    // ========== Internal Helpers ==========

    private void upsertJobs(List<Jobs> allJobs) {
        if (allJobs.isEmpty()) return;

        Set<String> newJobIds = allJobs.stream().map(Jobs::getId).collect(Collectors.toSet());

        Map<String, Jobs> existingJobsMap = jobsRepository.findAll().stream()
                .collect(Collectors.toMap(Jobs::getId, j -> j));

        List<Jobs> toSave = new ArrayList<>();

        for (Jobs job : allJobs) {
            Jobs existing = existingJobsMap.get(job.getId());
            if (existing != null) {
                existing.setPosition(job.getPosition());
                existing.setCompany(job.getCompany());
                existing.setLocation(job.getLocation());
                existing.setUrl(job.getUrl());
                existing.setDate(job.getDate());
                existing.setTags(job.getTags());
                existing.setDescription(job.getDescription());
                existing.setActive(true);
                toSave.add(existing);
            } else {
                job.setActive(true);
                toSave.add(job);
            }
        }

        // deactivate missing jobs
        existingJobsMap.values().forEach(job -> {
            if (!newJobIds.contains(job.getId()) && job.isActive()) {
                job.setActive(false);
                toSave.add(job);
            }
        });

        jobsRepository.saveAll(toSave);
    }

    // ========== Fetch APIs ==========

    private List<Jobs> fetchFromRemoteOk() {
        return fetchJobsFromApi("https://remoteok.com/api", "RemoteOk", null,
                node -> Jobs.builder()
                        .id("remoteok-" + node.path("id").asText())
                        .site("RemoteOk")
                        .position(node.path("position").asText())
                        .company(node.path("company").asText())
                        .location(node.path("location").asText())
                        .url(node.path("url").asText())
                        .date(node.path("date").asText())
                        .tags(parseTags(node.path("tags")))
                        .description(node.path("description").asText(""))
                        .build(), 1);
    }

    private List<Jobs> fetchFromRemotive() {
        return fetchJobsFromApi("https://remotive.com/api/remote-jobs", "Remotive", "jobs",
                node -> Jobs.builder()
                        .id("remotive-" + node.path("id").asText())
                        .site("Remotive")
                        .position(node.path("title").asText())
                        .company(node.path("company_name").asText())
                        .location(node.path("candidate_required_location").asText())
                        .url(node.path("url").asText())
                        .date(node.path("publication_date").asText())
                        .tags(parseTags(node.path("tags")))
                        .description(node.path("description").asText(""))
                        .build(), 0);
    }

    private List<Jobs> fetchFromArbeitnow() {
        return fetchJobsFromApi("https://www.arbeitnow.com/api/job-board-api", "Arbeitnow", "data",
                node -> Jobs.builder()
                        .id("arbeitnow-" + node.path("slug").asText())
                        .site("Arbeitnow")
                        .position(node.path("title").asText())
                        .company(node.path("company_name").asText())
                        .location(node.path("location").asText())
                        .url(node.path("url").asText())
                        .date("")
                        .tags("")
                        .description(node.path("description").asText(""))
                        .build(), 0);
    }

    private List<Jobs> fetchFromAdzuna() {
        String url = String.format(
                "https://api.adzuna.com/v1/api/jobs/in/search/1?app_id=%s&app_key=%s&results_per_page=20&content-type=application/json",
                "72b713aa", "a20e86c19a5a03c1c8bea22aa07c3a2c");

        return fetchJobsFromApi(url, "Adzuna", "results",
                node -> Jobs.builder()
                        .id("adzuna-" + node.path("id").asText())
                        .site("Adzuna")
                        .position(node.path("title").asText())
                        .company(node.path("company").path("display_name").asText())
                        .location(node.path("location").path("display_name").asText())
                        .url(node.path("redirect_url").asText())
                        .date(node.path("created").asText())
                        .tags("")
                        .description(node.path("description").asText(""))
                        .build(), 0);
    }

    private List<Jobs> fetchFromUsaJobs() {
        try {
            String url = "https://data.usajobs.gov/api/search?ResultsPerPage=20&Keyword=software";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "thakreshivam121@gmail.com");
            headers.set("Authorization-Key", "VYL4IstH31rQr5O4P7OzYbkMCff2W7zNvQuHvrqXiNs=");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            ArrayNode jobsArray = (ArrayNode) root.path("SearchResult").path("SearchResultItems");

            List<Jobs> results = new ArrayList<>();
            for (JsonNode item : jobsArray) {
                JsonNode node = item.path("MatchedObjectDescriptor");
                results.add(Jobs.builder()
                        .id("usajobs-" + node.path("PositionID").asText())
                        .site("USAJOBS")
                        .position(node.path("PositionTitle").asText())
                        .company(node.path("OrganizationName").asText())
                        .location(node.path("PositionLocationDisplay").asText())
                        .url(node.path("PositionURI").asText())
                        .date(node.path("PublicationStartDate").asText())
                        .tags("")
                        .description(node.path("UserArea").path("Details").path("JobSummary").asText(""))
                        .build());
            }
            logger.info("Fetched {} jobs from USAJOBS", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error fetching from USAJOBS: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Jobs> fetchFromWeWorkRemotely() {
        List<Jobs> results = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://weworkremotely.com/remote-jobs.rss")
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            Elements items = doc.select("item");
            for (Element item : items) {
                String title = item.select("title").text();
                String link = item.select("link").text();
                String description = item.select("description").text();
                String pubDate = item.select("pubDate").text();

                results.add(Jobs.builder()
                        .id("wework-" + link.hashCode())
                        .site("WeWorkRemotely")
                        .position(title)
                        .company("") // company not in RSS
                        .location("Remote")
                        .url(link)
                        .date(pubDate)
                        .tags("")
                        .description(description)
                        .build());
            }

            logger.info("Fetched {} jobs from WeWorkRemotely", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error fetching from WeWorkRemotely: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Jobs> fetchFromLinkedIn() {
        List<Jobs> results = new ArrayList<>();
        try {
            String baseUrl = "https://www.linkedin.com/jobs/search/?currentJobId=4292166388&f_TPR=r604800&keywords=remote%20developer&origin=JOB_SEARCH_PAGE_JOB_FILTER";

            int maxPages = 5; // fetch first 10 pages (25 jobs per page)
            for (int page = 0; page < maxPages; page++) {
                String url = baseUrl + (page * 25);

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .referrer("https://google.com")
                        .timeout(20000)
                        .get();

                Elements jobCards = doc.select(".base-card");
                if (jobCards.isEmpty()) break; // stop if no more jobs found

                for (Element card : jobCards) {
                    String title = card.select(".base-search-card__title").text();
                    String company = card.select(".base-search-card__subtitle a").text();
                    String location = card.select(".job-search-card__location").text();
                    String jobUrl = card.select("a.base-card__full-link").attr("href");

                    Jobs job = Jobs.builder()
                            .id("linkedin-" + jobUrl.hashCode())
                            .site("LinkedIn")
                            .position(title)
                            .company(company)
                            .location(location)
                            .url(jobUrl)
                            .date("") // LinkedIn doesn’t expose date directly
                            .tags("")
                            .description("") // Needs a second request to job detail (optional)
                            .active(true)
                            .build();

                    results.add(job);
                }

                logger.info("Fetched {} jobs from LinkedIn page {}", jobCards.size(), page + 1);

                // Small delay to reduce risk of blocking
                Thread.sleep(2000);
            }

            logger.info("Total LinkedIn jobs fetched: {}", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from LinkedIn: {}", e.getMessage());
        }
        return results;
    }

    public List<Jobs> getAllJobs() {
        return jobsRepository.findAllJobs();
    }


    // ========== Generic Fetcher ==========

    private interface JobMapper {
        Jobs map(JsonNode node);
    }

    private List<Jobs> fetchJobsFromApi(String url, String site, String arrayField,
                                        JobMapper mapper, int skipFirst) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            ArrayNode jobsArray = arrayField == null ? (ArrayNode) root : (ArrayNode) root.path(arrayField);

            List<Jobs> results = new ArrayList<>();
            int index = 0;
            for (JsonNode node : jobsArray) {
                if (index++ < skipFirst) continue;
                results.add(mapper.map(node));
            }
            logger.info("Fetched {} jobs from {}", results.size(), site);
            return results;
        } catch (Exception e) {
            logger.error("Error fetching from {}: {}", site, e.getMessage());
            return Collections.emptyList();
        }
    }

    private String parseTags(JsonNode tagsNode) {
        if (tagsNode.isArray()) {
            return StreamSupport.stream(tagsNode.spliterator(), false)
                    .map(JsonNode::asText)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }
}
