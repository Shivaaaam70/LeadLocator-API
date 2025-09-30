package com.example.Company_Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient ycWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://yc-oss.github.io/api/companies")
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) // 16MB buffer
                )
                .build();
    }
}
