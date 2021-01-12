/**
 * Class to retrieve credentials from AWS secrets manager.
 */
package com.jpmorgan.aws.amfinance.service;

import java.util.Map;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("SecretsRetrievalService")
@Slf4j
public class SecretsRetrievalService {

	@Bean
	public Function<Map<String, String>, String> fetchSecretDetails() {

		log.info("Inside retrieveCredentials...");

		return auditId -> "Secret retrived";
	}

}