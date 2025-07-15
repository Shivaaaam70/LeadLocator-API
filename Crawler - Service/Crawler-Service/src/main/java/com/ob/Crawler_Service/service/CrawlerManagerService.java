package com.ob.Crawler_Service.service;

import com.ob.Crawler_Service.config.SiteConfig;
import com.ob.Crawler_Service.entity.JobPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager that orchestrates crawling by selectiong crawler service
 * based on the site configuration
 */
@Service
public class CrawlerManagerService {

    @Autowired
    private DynamicCrawlerService dynamicCrawlerService;

    @Autowired
    private SeleniumCrawlerService seleniumCrawlerService;

    /**
     * Crawl jobs from the sites describe by the config.
     * Uses Jsoup or Selenium accordingly
     */
    public List<JobPost> crawlSite(SiteConfig config){
        if(config.isUseSelenium()){
            //use selenium crawler for dynamic JS-heavy sites
            return seleniumCrawlerService.crawlWithSelenium(config);
        }else{
            //use Jsoup crawler for retrices and pagination
           return dynamicCrawlerService.crawlWithPaginationAndRetries(config);
        }
    }
}
