package com.ob.Crawler_Service.service;

import com.ob.Crawler_Service.config.SiteConfig;
import com.ob.Crawler_Service.entity.JobPost;
import com.ob.Crawler_Service.repository.JobPostRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//Uses Jsoup to crawl static sites with pagination, retry and delay
@Service
public class DynamicCrawlerService {

    @Autowired
    private JobPostRepository jobPostRepository;

    private static final Logger logger= LoggerFactory.getLogger(DynamicCrawlerService.class);


    public List<JobPost> crawlWithPaginationAndRetries(SiteConfig config) {
        List<JobPost> allJobs = new ArrayList<>();
        int retries = 0;

        int currentPage = config.getPaginationConfig().getStartPage();
        int maxPages = config.getPaginationConfig().getMaxPages();

        while (currentPage <= maxPages) {
            String pageUrl = buildUrlPage(config.getBaseUrl(), config.getPaginationConfig(), currentPage);

            try {
                logger.info("Crawling page URL: {}", pageUrl);

                List<JobPost> jobs = crawlPage(pageUrl, config.getSelectors());
                allJobs.addAll(jobs);

                retries = 0; // Reset retries on success
                currentPage++;

                Thread.sleep(config.getDelayBetweenRequestsMs());  // Crawl delay

            } catch (IOException | InterruptedException e) {
                logger.error("Error crawling {}: {}", pageUrl, e.getMessage());

                if (++retries > config.getMaxRetries()) {
                    logger.error("Max retries reached for {}, stopping crawl.", pageUrl);
                    break;
                }
                try {
                    // Exponential backoff delay on retry
                    Thread.sleep(1000L * retries);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return allJobs;
    }

    /**
     * Builds page URL based on pagination config (param increment style).
     */
    private String buildUrlPage(String baseUrl,SiteConfig.PaginationConfig paginationConfig, int page){
        if(paginationConfig.getType()==SiteConfig.PaginationConfig.PaginationType.PARAM_INCREMENT){
            String seperator=baseUrl.contains("?") ? "&" : "?";
            return baseUrl+seperator+paginationConfig.getPaginationParamName()+"="+page;
        }

        //NEXT BUTTON pagination requires selenium not handled here
        return baseUrl;
    }

    /**
     * Crawl a single page for job listings using CSS selectors.
     */
    private List<JobPost> crawlPage(String url, Map<String,String> selectors)throws IOException
    {
        List<JobPost> jobs=new ArrayList<>();

        //fetch and parse the HTML pages
        Document doc= Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; JobCrawler/1.0)")
                .timeout(1500)
                .get();

        //Validate and fetch job listing selectors
        String jobElementSelector=selectors.get("jobElement");
        if(jobElementSelector==null || jobElementSelector.isEmpty()) {
            logger.error("Missing or empty selector for 'jobElement'.Cannot crawl page: {}", url);
            return jobs;
        }

        //Select all jobs listing elements
       Elements jobElements = doc.select(jobElementSelector);

       for(Element jobE: jobElements){
           String title=safeSelectText(jobE,selectors.get("title"));
           String company=safeSelectText(jobE,selectors.get("company"));
           String jobUrl=safeSelectText(jobE,selectors.get("urlAttribute"));

           if(title!=null && !title.isEmpty() && jobUrl!=null && !jobUrl.isEmpty()){
               //Avoid duplicates based on the url
               if(!jobPostRepository.existsByUrl(jobUrl)){
                   JobPost job=new JobPost(null,title, company!=null ? company : "N/A", jobUrl);
                   jobPostRepository.save(job);
                   jobs.add(job);
               }
           }

       }
       return jobs;
    }

    /**
     * Safely select text from element using CSS selector.
     */
    private String safeSelectText(Element parent, String selector) {
        if (parent == null || selector == null || selector.isEmpty()) {
            return null;
        }
        try {
            Element el = parent.selectFirst(selector);
            return el != null ? el.text() : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    /**
     * Safely select URL attribute value from element.
     */
    private String safeSelectUrl(Element parent, String attribute) {
        if (parent == null || attribute == null || attribute.isEmpty()) {
            return null;
        }
        Element link = parent.selectFirst("a[href]");
        return link != null ? link.absUrl(attribute) : null;
    }


}


