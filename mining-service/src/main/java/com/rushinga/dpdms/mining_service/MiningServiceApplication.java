package com.rushinga.dpdms.mining_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class MiningServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiningServiceApplication.class, args);
	}

}
