package com.ob.Crawler_Service.controller;

import com.ob.Crawler_Service.config.SiteConfig;
import com.ob.Crawler_Service.entity.JobPost;
import com.ob.Crawler_Service.service.CrawlerManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Simple controller to trigger the crawler sites
 */
@RestController
@RequestMapping("/api/crawl")
public class JobPostController {

    @Autowired
    private CrawlerManagerService service;

    @Autowired
    private SiteConfig siteConfigLinkedin;

    @Autowired
    private SiteConfig siteConfigDynamicSite;


    /**
     * Crawl Linkedin jobs
     */
    @GetMapping("/linkedin")
    public List<JobPost> crawlLinkedin(){
        return service.crawlSite(siteConfigLinkedin);
    }

    /**
     * Crawl Dynamic JS sites
     */
    @GetMapping("/dynamic")
    public List<JobPost>crawlDynamic(){
        return service.crawlSite(siteConfigDynamicSite);

    }

    @PostMapping("/dynamic-crawl")
    public List<JobPost> crawlDynamic(@RequestBody SiteConfig config) {
        return service.crawlSite(config);
    }


}
