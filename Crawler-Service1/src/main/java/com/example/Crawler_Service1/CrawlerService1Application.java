package com.example.Crawler_Service1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CrawlerService1Application {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerService1Application.class, args);
	}

}
