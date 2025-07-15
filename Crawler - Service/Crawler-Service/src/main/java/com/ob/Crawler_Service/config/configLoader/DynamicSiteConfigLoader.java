package com.ob.Crawler_Service.config.configLoader;

import com.ob.Crawler_Service.config.SiteConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicSiteConfigLoader{

    @Bean
    public SiteConfig siteConfigDynamicSite(){

            SiteConfig siteConfig=new SiteConfig();
            siteConfig.setName("justRemote");
            siteConfig.setBaseUrl("https://justremote.co/remote-jobs");
            siteConfig.setUseSelenium(true);
            siteConfig.setMaxRetries(5);
            siteConfig.setDelayBetweenRequestsMs(3000);

            SiteConfig.PaginationConfig pagination=new SiteConfig.PaginationConfig();

            pagination.setType(SiteConfig.PaginationConfig.PaginationType.PARAM_INCREMENT);
            pagination.setPaginationParamName("page");
            pagination.setStartPage(1);
            pagination.setMaxPages(5);
            siteConfig.setPaginationConfig(pagination);

            Map<String,String> selectors=new HashMap<>();
            selectors.put("jobElements", ".job-listing");
            selectors.put("title", ".job-title");
            selectors.put("company", ".company-name");
            selectors.put("urlAttribute", "href");
            siteConfig.setSelectors(selectors);

            return siteConfig;
        }
    }
