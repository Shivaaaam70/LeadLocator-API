package com.ob.Crawler_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CrawlerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerServiceApplication.class, args);
	}

}
