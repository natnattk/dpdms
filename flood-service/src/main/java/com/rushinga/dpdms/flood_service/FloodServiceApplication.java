package com.rushinga.dpdms.flood_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class FloodServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FloodServiceApplication.class, args);
	}

}
