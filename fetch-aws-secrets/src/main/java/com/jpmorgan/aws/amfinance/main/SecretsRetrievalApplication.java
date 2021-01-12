package com.jpmorgan.aws.amfinance.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.jpmorgan.aws" })
public class SecretsRetrievalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecretsRetrievalApplication.class, args);
	}

}