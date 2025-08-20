package com.ob.Crawler_Service.config.configLoader;

import com.ob.Crawler_Service.config.SiteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * Hardcoded demo configs for two sites
 */

@Configuration
public class siteConfigLoader {

    @Bean
    public SiteConfig siteConfigLinkedin(){

        SiteConfig siteConfig=new SiteConfig();
        siteConfig.setName("Linkedin");
        siteConfig.setBaseUrl("https://www.linkedin.com/jobs/");
        siteConfig.setUseSelenium(true);
        siteConfig.setMaxRetries(10);
        siteConfig.setDelayBetweenRequestsMs(2000);


        SiteConfig.PaginationConfig pagination=new SiteConfig.PaginationConfig();
        pagination.setType(SiteConfig.PaginationConfig.PaginationType.PARAM_INCREMENT);
        pagination.setPaginationParamName("page");
        pagination.setStartPage(1);
        pagination.setMaxPages(5);
        siteConfig.setPaginationConfig(pagination);


        Map<String, String> selectors = new HashMap<>();
        selectors.put("jobElement", ".job-listing");
        selectors.put("title", ".job-title");
        selectors.put("company", ".company-name");
        selectors.put("urlAttribute", "href");
        siteConfig.setSelectors(selectors);

        return siteConfig;

    }


}
