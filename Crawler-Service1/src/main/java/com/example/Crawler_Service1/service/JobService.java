package com.example.Crawler_Service1.service;

import com.example.Crawler_Service1.entity.Jobs;
import com.example.Crawler_Service1.repository.JobsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JobService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobsRepository jobsRepository;

    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    public void fetchAndStoreAllJobs() throws JsonProcessingException {
        List<Jobs> jobs = new ArrayList<>();
        jobs.addAll(fetchFromRemoteOk());
        jobs.addAll(fetchFromArbeitnow());
        jobs.addAll(fetchFromAdzuna());
        jobs.addAll(fetchFromRemotive());
        jobs.addAll(fetchFromUsaJobs());
        jobs.addAll(fetchFromWeWorkRemotely());

        logger.info("Saving {} jobs to database.", jobs.size());
        jobsRepository.saveAll(jobs);
    }

    public List<Jobs> getAllJobsFromDb() {
        return jobsRepository.findAllJobs();
    }

    public List<Jobs> getJobsByKeyword(String keyword) {
        return jobsRepository.findByPositionContainingIgnoreCase(keyword);
    }

    // ------------------- RemoteOk -------------------
    private List<Jobs> fetchFromRemoteOk() {
        List<Jobs> results = new ArrayList<>();
        try {
            String url = "https://remoteok.com/api";
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(json);

            for (int i = 1; i < root.size(); i++) {
                JsonNode node = root.get(i);
                Jobs job = Jobs.builder()
                        .id("remoteok-" + node.path("id").asText())
                        .site("RemoteOk")
                        .position(node.path("position").asText())
                        .company(node.path("company").asText())
                        .location(node.path("location").asText())
                        .url(node.path("url").asText())
                        .date(node.path("date").asText())
                        .tags(parseTags(node.path("tags")))
                        .description(node.path("description").asText(""))
                        .build();
                results.add(job);
            }

            logger.info("Fetched {} jobs from RemoteOk", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from RemoteOK: {}", e.getMessage());
        }
        return results;
    }

    // ------------------- Remotive -------------------
    private List<Jobs> fetchFromRemotive() {
        List<Jobs> results = new ArrayList<>();
        try {
            String url = "https://remotive.com/api/remote-jobs";
            String json = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(json);
            ArrayNode jobsArray = (ArrayNode) root.path("jobs");
            for (JsonNode node : jobsArray) {
                Jobs job = Jobs.builder()
                        .id("remotive-" + node.path("id").asText())
                        .site("Remotive")
                        .position(node.path("title").asText())
                        .company(node.path("company_name").asText())
                        .location(node.path("candidate_required_location").asText())
                        .url(node.path("url").asText())
                        .date(node.path("publication_date").asText())
                        .tags(parseTags(node.path("tags")))
                        .description(node.path("description").asText(""))
                        .build();
                results.add(job);
            }
            logger.info("Fetched {} jobs from Remotive", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from Remotive: {}", e.getMessage());
        }
        return results;
    }

    // ------------------- Arbeitnow -------------------
    private List<Jobs> fetchFromArbeitnow() {
        List<Jobs> results = new ArrayList<>();
        try {
            String url = "https://www.arbeitnow.com/api/job-board-api";
            String json = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(json);
            ArrayNode jobsArray = (ArrayNode) root.path("data");

            for (JsonNode node : jobsArray) {
                Jobs job = Jobs.builder()
                        .id("arbeitnow-" + node.path("slug").asText())
                        .site("Arbeitnow")
                        .position(node.path("title").asText())
                        .company(node.path("company_name").asText())
                        .location(node.path("location").asText())
                        .url(node.path("url").asText())
                        .date("")
                        .tags("")
                        .description(node.path("description").asText(""))
                        .build();
                results.add(job);
            }
            logger.info("Fetched {} jobs from Arbeitnow", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from Arbeitnow: {}", e.getMessage());
        }
        return results;
    }

    // ------------------- Adzuna -------------------
    private List<Jobs> fetchFromAdzuna() {
        List<Jobs> results = new ArrayList<>();
        try {
            String appId = "72b713aa";
            String appKey = "a20e86c19a5a03c1c8bea22aa07c3a2c";
            String country = "in";

            String url = String.format(
                    "https://api.adzuna.com/v1/api/jobs/%s/search/1?app_id=%s&app_key=%s&results_per_page=20&content-type=application/json",
                    country, appId, appKey
            );
            String json = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = objectMapper.readTree(json);
            ArrayNode jobsArray = (ArrayNode) rootNode.path("results");

            for (JsonNode node : jobsArray) {
                Jobs job = Jobs.builder()
                        .id("adzuna-" + node.path("id").asText())
                        .site("Adzuna")
                        .position(node.path("title").asText())
                        .company(node.path("company").path("display_name").asText())
                        .location(node.path("location").path("display_name").asText())
                        .url(node.path("redirect_url").asText())
                        .date(node.path("created").asText())
                        .tags("")
                        .description(node.path("description").asText(""))
                        .build();
                results.add(job);
            }
            logger.info("Fetched {} jobs from Adzuna", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from Adzuna: {}", e.getMessage());
        }
        return results;
    }

    // ------------------- USAJobs -------------------
    private List<Jobs> fetchFromUsaJobs() {
        List<Jobs> results = new ArrayList<>();
        try {
            String url = "https://data.usajobs.gov/api/search?ResultsPerPage=20&Keyword=software";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "thakreshivam121@gmail.com");
            headers.set("Authorization-Key", "VYL4IstH31rQr5O4P7OzYbkMCff2W7zNvQuHvrqXiNs=");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            ArrayNode jobsArray = (ArrayNode) root.path("SearchResult").path("SearchResultItems");

            for (JsonNode item : jobsArray) {
                JsonNode node = item.path("MatchedObjectDescriptor");
                Jobs job = Jobs.builder()
                        .id("usajobs-" + node.path("PositionID").asText())
                        .site("USAJOBS")
                        .position(node.path("PositionTitle").asText())
                        .company(node.path("OrganizationName").asText())
                        .location(node.path("PositionLocationDisplay").asText())
                        .url(node.path("PositionURI").asText())
                        .date(node.path("PublicationStartDate").asText())
                        .tags("")
                        .description(node.path("UserArea").path("Details").path("JobSummary").asText(""))
                        .build();
                results.add(job);
            }
            logger.info("Fetched {} jobs from USAJOBS", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from USAJOBS: {}", e.getMessage());
        }
        return results;
    }

    private List<Jobs> fetchFromWeWorkRemotely() {
        List<Jobs> results = new ArrayList<>();
        try {
            String url = "https://weworkremotely.com/remote-jobs.rss";

            // Fetch RSS feed (XML mode)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .ignoreContentType(true) // important for XML
                    .timeout(10000)
                    .get();

            Elements items = doc.select("item");

            for (Element item : items) {
                String title = item.select("title").first() != null ? item.select("title").first().text() : "";
                String link = item.select("link").first() != null ? item.select("link").first().text() : "";
                String description = item.select("description").first() != null ? Jsoup.parse(item.select("description").first().text()).text() : "";
                String pubDate = item.select("pubDate").first() != null ? item.select("pubDate").first().text() : "";

                // WWR titles are like: "Company: Job Title"
                String company = "";
                String position = title;
                if (title.contains(":")) {
                    String[] parts = title.split(":", 2);
                    company = parts[0].trim();
                    position = parts[1].trim();
                }

                Jobs job = Jobs.builder()
                        .id("weworkremotely-" + link.hashCode())
                        .site("WeWorkRemotely")
                        .position(position)
                        .company(company)
                        .location("Remote")
                        .url(link)
                        .date(pubDate)
                        .tags("")
                        .description(description)
                        .build();

                results.add(job);
            }

            logger.info("Fetched {} jobs from WeWorkRemotely", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from WeWorkRemotely", e);
        }
        return results;
    }


    // ------------------- Tag Parser -------------------
    private String parseTags(JsonNode node) {
        try {
            if (node.isArray()) {
                return StreamSupport.stream(node.spliterator(), false)
                        .map(JsonNode::asText)
                        .collect(Collectors.joining(","));
            }
        } catch (Exception ignored) {}
        return "";
    }
}
