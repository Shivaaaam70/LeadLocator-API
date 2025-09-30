package com.example.Company_Service.service;

import com.example.Company_Service.entity.Company;
import com.example.Company_Service.repository.CompanyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CrawlerService {

    private final WebClient webClient;
    private final CompanyRepository repo;
    private final ObjectMapper objectMapper;

    @Value("${yc.api.all}")
    private String allCompaniesUrl;

    @Value("${yc.api.top}")
    private String topCompaniesUrl;

    @Value("${yc.api.hiring}")
    private String hiringCompaniesUrl;

    private static final int BATCH_SIZE = 500; // adjust for performance

    public CrawlerService(WebClient.Builder builder, CompanyRepository repo, ObjectMapper objectMapper) {
        this.webClient = builder.build();
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    public boolean crawlAllEndpoints() {
        AtomicInteger upserts = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();

        boolean ok1 = crawlAndSave(allCompaniesUrl, upserts, false, false, errors);
        boolean ok2 = crawlAndSave(topCompaniesUrl, upserts, true, false, errors);
        boolean ok3 = crawlAndSave(hiringCompaniesUrl, upserts, false, true, errors);

        System.out.printf("✅ Crawl finished: %d companies inserted/updated, %d errors%n",
                upserts.get(), errors.get());

        return ok1 && ok2 && ok3 && errors.get() == 0;
    }

    private boolean crawlAndSave(String url, AtomicInteger upserts,
                                 boolean topFlag, boolean hiringFlag, AtomicInteger errors) {
        try {
            String jsonResponse = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(jsonResponse);

            List<Company> batch = new ArrayList<>(BATCH_SIZE);
            Set<String> seenIds = new HashSet<>();

            for (JsonNode node : root) {
                String ycId = node.path("id").asText();
                if (ycId == null || ycId.isEmpty()) continue;

                // Deduplicate within this crawl batch
                if (!seenIds.add(ycId)) continue;

                Company company = mapJsonToCompany(node, topFlag, hiringFlag);
                batch.add(company);

                if (batch.size() >= BATCH_SIZE) {
                    flushBatch(batch, upserts);
                    seenIds.clear();
                }
            }

            // flush leftover records
            flushBatch(batch, upserts);
            return true;

        } catch (Exception e) {
            System.err.printf("❌ Error fetching %s : %s%n", url, e.getMessage());
            errors.incrementAndGet();
            return false;
        }
    }

    private void flushBatch(List<Company> batch, AtomicInteger upserts) {
        if (batch.isEmpty()) return;

        long t0 = System.currentTimeMillis();
        int[][] results = repo.batchUpsert(batch); // ✅ insert new + update existing
        long took = System.currentTimeMillis() - t0;

        upserts.addAndGet(results.length);

        System.out.printf("➡️ Flushed %d companies (inserted/updated) in %d ms%n",
                batch.size(), took);

        batch.clear();
    }

    private Company mapJsonToCompany(JsonNode node, boolean topFlag, boolean hiringFlag) {
        String ycId = node.path("id").asText();
        String name = node.path("name").asText();

        // description fallback
        String desc = node.hasNonNull("description") ? node.get("description").asText() : null;
        if ((desc == null || desc.isBlank()) && node.hasNonNull("long_description")) {
            desc = node.get("long_description").asText();
        }
        if ((desc == null || desc.isBlank()) && node.hasNonNull("one_liner")) {
            desc = node.get("one_liner").asText();
        }

        String homepage = node.path("url").asText();
        String domain = node.path("domain").asText();

        // locations
        String locations = "";
        if (node.has("locations") && node.get("locations").isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode loc : node.get("locations")) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(loc.asText());
            }
            locations = sb.toString();
        }

        Integer teamSize = node.hasNonNull("team_size") ? node.get("team_size").asInt() : null;

        // tags
        String tags = "";
        if (node.has("tags") && node.get("tags").isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode tag : node.get("tags")) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(tag.asText());
            }
            tags = sb.toString();
        }

        // last updated (optional future use)
        Instant lastUpdated = null;
        if (node.has("last_funded_at") && !node.get("last_funded_at").asText().isEmpty()) {
            try {
                lastUpdated = Instant.parse(node.get("last_funded_at").asText());
            } catch (Exception ignore) {}
        }

        Company company = new Company();
        company.setYcId(ycId);
        company.setName(name);
        company.setDescription(desc);
        company.setHomepage(homepage);
        company.setDomain(domain);
        company.setLocations(locations);
        company.setTeamSize(teamSize);
        company.setTags(tags);
        company.setHiringFlag(hiringFlag);
        company.setTopFlag(topFlag);
        company.setRawJson(node.toString());

        return company;
    }
}
