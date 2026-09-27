package com.rushinga.dpdms.zoonotic_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class ZoonoticServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZoonoticServiceApplication.class, args);
	}

}
