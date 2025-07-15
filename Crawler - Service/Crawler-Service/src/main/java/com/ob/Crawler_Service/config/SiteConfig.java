package com.ob.Crawler_Service.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Configuration data for crawling a specific site.
 * Includes base URL, selectors, pagination config, and flags.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SiteConfig{
    private String name;           //Name of the site
    private String baseUrl;        //Url of that site
    private boolean useSelenium;    //Using the selenium
    private int maxRetries;          //Max Retries on the error
    private int delayBetweenRequestsMs;  //Delay between Requests to avoid overload


    private PaginationConfig paginationConfig;

    private Map<String,String> selectors;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaginationConfig{

        public enum PaginationType{
            PARAM_INCREMENT, NEXT_BUTTON
        }

        private PaginationType type;   //How to paginate
        private String paginationParamName;  //"page" from url param
        private String nextButtonSelectors;  //CSS selectors for next button
        private int startPage;
        private int maxPages;
    }
}
