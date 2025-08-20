package com.example.Crawler_Service1.service;
import com.example.Crawler_Service1.entity.Jobs;
import com.example.Crawler_Service1.repository.JobsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    /**
     * RestTemplate allows you to make HTTP requests to other APIs (like RemoteOk)
     * Used here to fetch job data from external APIs.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * ObjectMapper is from Jackson
     * and used to parse JSON responses into Java objects.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Your repository (using Spring Data JPA)
     * allows interacting with the database — for saving jobs and fetching jobs.
     */
    @Autowired
    private JobsRepository jobsRepository;

    /**
     * Logger used for printing info and error logs,
     * useful for debugging and monitoring.
     */
    private final Logger logger= LoggerFactory.getLogger(JobService.class);

    /**
     * This method triggers fetching jobs from external APIs
     * and saving them into your database.
     */
    public void fetchAndStoreAllJobs() throws JsonProcessingException {
        List<Jobs> jobs = new ArrayList<>();
        jobs.addAll(fetchFromRemoteOk());
        jobs.addAll(fetchFromArbeitnow());
        jobs.addAll(fetchFromAdzuna());
        jobs.addAll(fetchFromRemotive());
        jobs.addAll(fetchFromUsaJobs());
//        jobs.addAll(fetchFromWeWorkRemotely());
//        jobs.addAll(fetchFromWorkingNomads());


        logger.info("Saving {} jobs to database.", jobs.size());
        jobsRepository.saveAll(jobs);
    }

    /**
     * Fetches all jobs from your database using your repository.
     * Useful for the "get all jobs" endpoint.
     */
    public List<Jobs> getAllJobsFromDb() {
        return jobsRepository.findAllJobs();
    }

    /**
     *Searches the database for jobs whose position contains the keyword (case-insensitive)
     * Useful for the "search by keyword" API.
     */
    public List<Jobs> getJobsByKeyword(String keyword) {
        return jobsRepository.findByPositionContainingIgnoreCase(keyword);
    }


    private List<Jobs> fetchFromRemoteOk(){
        List<Jobs> results=new ArrayList<>();
        try{
            /**
             * Calls RemoteOk’s API to get job data in JSON format.
             * Parses the JSON response using ObjectMapper.
             */
            String url="https://remoteok.com/api";
            String json=restTemplate.getForObject(url, String.class);
            JsonNode root=objectMapper.readTree(json);

            /**
             * Skips index 0 (since RemoteOk sometimes sends metadata at index 0).
             * Loops through each job node in the JSON
             */
            for (int i = 1; i < root.size(); i++) {
                JsonNode node = root.get(i);

                /**
                 * Creates a new Jobs object for each job found in the JSON.
                 * Maps JSON fields to your entity fields.
                 * Calls parseTags() to handle the tags as a comma-separated string.
                 */
                Jobs job = Jobs.builder()
                        .id("remoteok-" + node.path("id").asText())
                        .site("RemoteOk")
                        .position(node.path("position").asText())
                        .company(node.path("company").asText())
                        .location(node.path("location").asText())
                        .url(node.path("url").asText())
                        .date(node.path("date").asText())
                        .tags(parseTags(node))
                        .build();

                /**
                 * Adds each job to the results list.
                 */
                results.add(job);
            }

            /**
             * Logs the number of jobs fetched.
             * Catches and logs any errors during fetching/parsing.
             * Returns the list of jobs.
             */
            logger.info("Fetched {} jobs from RemoteOk", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from RemoteOK: {}", e.getMessage());
        }
        return results;

        }
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
                        .build();

                results.add(job);
            }
            logger.info("Fetched {} jobs from Remotive", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from Remotive: {}", e.getMessage());
        }
        return results;
    }

   //Fetch the jobs From Arbeitnow
    private List<Jobs> fetchFromArbeitnow(){
        List<Jobs> results=new ArrayList<>();
            try {
                String url = "https://www.arbeitnow.com/api/job-board-api";
                String json = restTemplate.getForObject(url, String.class);

                JsonNode root = objectMapper.readTree(json);
                ArrayNode jobsArray = (ArrayNode) root.path("data");

                    for (JsonNode node : jobsArray) {
                        Jobs job = Jobs.builder()
                                .id("Arbeitnow-" + node.path("slug").asText())
                                .site("Arbeitnow")
                                .position(node.path("title").asText())
                                .company(node.path("company_name").asText())
                                .location(node.path("location").asText())
                                .url(node.path("url").asText())
                                .date("")
                                .tags("")
                                .build();

                        results.add(job);
                    }
                } catch(Exception e){
                    logger.error("Error fetching from Arbeitnow: {}", e.getMessage());
                }
            return results;
    }
    private List<Jobs> fetchFromAdzuna(){
        List<Jobs> results=new ArrayList<>();
        try{
            String appId="72b713aa";
            String appKey="a20e86c19a5a03c1c8bea22aa07c3a2c";
            String country="in";

            String url = String.format(
                    "https://api.adzuna.com/v1/api/jobs/%s/search/1?app_id=%s&app_key=%s&results_per_page=20&content-type=application/json",
                    country, appId, appKey
            );
            String json=restTemplate.getForObject(url,String.class);
            JsonNode rootNode=objectMapper.readTree(json);
            ArrayNode jobsArray=(ArrayNode) rootNode.path("results");

            for (JsonNode node : jobsArray) {
                Jobs job = Jobs.builder()
                        .id("adzuna-" + node.path("id").asText())
                        .site("Adzuna")
                        .position(node.path("title").asText())
                        .company(node.path("company").path("display_name").asText())
                        .location(node.path("location").path("display_name").asText())
                        .url(node.path("redirect_url").asText())
                        .date(node.path("created").asText())
                        .tags("")  // Adzuna does not provide tags directly
                        .build();

                results.add(job);
            }

            logger.info("Fetched {} jobs from Adzuna", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from Adzuna: {}", e.getMessage());
        }
        return results;

    }

    /**
     * Example for fetching from WeWorkRemotely API
     */
//    private List<Jobs> fetchFromWeWorkRemotely() {
//        List<Jobs> results = new ArrayList<>();
//        try {
//            String url = "https://weworkremotely.com/remote-jobs.json"; // Internal JSON endpoint
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", "Mozilla/5.0"); // Mimic browser
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//
//            JsonNode root = objectMapper.readTree(response.getBody());
//            ArrayNode jobsArray = (ArrayNode) root.path("jobs");
//
//            for (JsonNode jobNode : jobsArray) {
//                String jobUrl = "https://weworkremotely.com" + jobNode.path("url").asText();
//                String title = jobNode.path("title").asText();
//                String company = jobNode.path("company_name").asText();
//                String location = jobNode.path("location").asText();
//                String date = jobNode.path("publication_date").asText();
//                String tags = parseTags(jobNode);
//
//                if (title.isEmpty() || company.isEmpty()) continue;
//
//                Jobs job = Jobs.builder()
//                        .id("wwr-" + jobUrl.hashCode())
//                        .site("WeWorkRemotely")
//                        .position(title)
//                        .company(company)
//                        .location(location)
//                        .url(jobUrl)
//                        .date(date)
//                        .tags(tags)
//                        .build();
//
//                results.add(job);
//            }
//
//            logger.info("Fetched {} jobs from WeWorkRemotely", results.size());
//        } catch (Exception e) {
//            logger.error("Error fetching WeWorkRemotely jobs", e);
//        }
//        return results;
//    }

    /**
     * Example for fetching from WorkingNomads API
     */
//    private List<Jobs> fetchFromWorkingNomads() {
//        List<Jobs> results = new ArrayList<>();
//        try {
//            String url = "https://www.workingnomads.com/api/jobs";
//            String json = restTemplate.getForObject(url, String.class);
//            ArrayNode jobsArray = (ArrayNode) objectMapper.readTree(json);
//
//            for (JsonNode node : jobsArray) {
//                Jobs job = Jobs.builder()
//                        .id("wn-" + node.path("id").asText())
//                        .site("WorkingNomads")
//                        .position(node.path("title").asText())
//                        .company(node.path("company").asText())
//                        .location(node.path("location").asText())
//                        .url(node.path("url").asText())
//                        .date("") // No date provided
//                        .tags(parseTags(node.path("tags")))
//                        .build();
//                results.add(job);
//            }
//            logger.info("Fetched {} jobs from WorkingNomads", results.size());
//        } catch (Exception e) {
//            logger.error("Error fetching from WorkingNomads: {}", e.getMessage());
//        }
//        return results;
//    }

    private List<Jobs> fetchFromUsaJobs() {
        List<Jobs> results = new ArrayList<>();
        try {
            String url = "https://data.usajobs.gov/api/search?ResultsPerPage=20&Keyword=software";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "thakreshivam121@gmail.com"); // Replace with your email
            headers.set("Authorization-Key", "VYL4IstH31rQr5O4P7OzYbkMCff2W7zNvQuHvrqXiNs=");   // Replace with your key

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            ArrayNode jobsArray = (ArrayNode) root
                    .path("SearchResult")
                    .path("SearchResultItems");

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
                        .tags("") // USAJOBS doesn't provide tags directly
                        .build();

                results.add(job);
            }

            logger.info("Fetched {} jobs from USAJOBS", results.size());
        } catch (Exception e) {
            logger.error("Error fetching from USAJOBS: {}", e.getMessage());
        }
        return results;
    }

    /**
     * Converts the tags (from JSON array) into a comma-separated string for storage.
     * If no tags, returns an empty string.
     */
    private String parseTags(JsonNode node) {
        try {
            ArrayNode tagsNode = (ArrayNode) node.path("tags");
            if (tagsNode != null && tagsNode.isArray() && tagsNode.size() > 0) {
                return String.join(",", objectMapper.convertValue(tagsNode, List.class));
            }
        } catch (Exception ignored) {
        }
        return "";
    }
    }


