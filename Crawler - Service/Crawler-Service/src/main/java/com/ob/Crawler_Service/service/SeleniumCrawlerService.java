package com.ob.Crawler_Service.service;

import com.ob.Crawler_Service.config.SiteConfig;
import com.ob.Crawler_Service.entity.JobPost;
import com.ob.Crawler_Service.repository.JobPostRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeleniumCrawlerService {

    @Autowired
    private JobPostRepository jobPostRepository;

    private static final Logger logger = LoggerFactory.getLogger(SeleniumCrawlerService.class);

    /**
     * Crawl site using Selenium according to config.
     */
    public List<JobPost> crawlWithSelenium(SiteConfig config) {
        List<JobPost> jobs = new ArrayList<>();
        WebDriver driver = null;

        try {

            // Set up headless ChromeDriver

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            driver = new ChromeDriver(options);

            int currentPage = config.getPaginationConfig().getStartPage();
            int maxPages = config.getPaginationConfig().getMaxPages();

            while (currentPage <= maxPages) {
                String pageUrl = buildPageUrl(config.getBaseUrl(), config.getPaginationConfig(), currentPage);
                logger.info("Selenium crawling page: {}", pageUrl);

                driver.get(pageUrl);
                Thread.sleep(config.getDelayBetweenRequestsMs());

                List<WebElement> jobElements = driver.findElements(By.cssSelector(config.getSelectors().get("jobElement")));

                for (WebElement jobEl : jobElements) {
                    String title = safeFindElementText(jobEl, config.getSelectors().get("title"));
                    String company = safeFindElementText(jobEl, config.getSelectors().get("company"));
                    String jobUrl = safeFindElementAttribute(jobEl, "a", config.getSelectors().get("urlAttribute"));

                    if (title != null && !title.isEmpty() && jobUrl != null && !jobUrl.isEmpty()) {
                        if (!jobPostRepository.existsByUrl(jobUrl)) {
                            JobPost job = new JobPost(null,title, company != null ? company : "N/A", jobUrl);
                            jobPostRepository.save(job);
                            jobs.add(job);
                        }
                    }
                }
                currentPage++;
            }
        } catch (Exception e) {
            logger.error("Error during Selenium crawl: %s", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit(); // Clean up driver resources
            }
        }

        return jobs;
    }

    /**
     * Builds page URL for pagination.
     */
    private String buildPageUrl(String baseUrl, SiteConfig.PaginationConfig paginationConfig, int page) {
        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + paginationConfig.getPaginationParamName() + "=" + page;
    }

    /**
     * Helper to safely find child element text by CSS selector.
     */
    private String safeFindElementText(WebElement parent, String selector) {
        try {
            WebElement el = parent.findElement(By.cssSelector(selector));
            return el.getText();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Helper to safely find attribute value on child element.
     */
    private String safeFindElementAttribute(WebElement parent, String childSelector, String attribute) {
        try {
            WebElement el = parent.findElement(By.cssSelector(childSelector));
            return el.getAttribute(attribute);
        } catch (Exception e) {
            return null;
        }
    }
}
