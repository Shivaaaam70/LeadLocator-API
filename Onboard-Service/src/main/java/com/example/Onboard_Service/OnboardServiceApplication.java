package com.example.Onboard_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OnboardServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnboardServiceApplication.class, args);
	}

}
