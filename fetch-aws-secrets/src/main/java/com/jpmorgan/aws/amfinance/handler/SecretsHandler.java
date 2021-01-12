/**
 * AWS Handler class for fetching secrets from AWS secrets manager.
 */
package com.jpmorgan.aws.amfinance.handler;

import java.util.Map;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;
import org.springframework.stereotype.Component;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.jpmorgan.aws.amfinance.util.ServiceConstants;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component("SecretsHandler")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SecretsHandler extends SpringBootRequestHandler<Map<String, String>, Object> {

	@Override
	public Object handleRequest(Map<String, String> request, Context context) {

		return this.fetchSecretsFromSecertsMngr(request);
	}

	/**
	 * Fetch secret key and value for the given secretId.
	 * 
	 * @param {@linkplain Map<String, String>}.
	 * @return {@linkplain Object}.
	 */
	private Object fetchSecretsFromSecertsMngr(Map<String, String> request) {

		log.info("Retrieving the secret value for secretId {} from region {}", request.get("secretId"),
				request.get("region"));

		GetSecretValueResult getSecretValueResult = null;

		// Create end-point configuration
		AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
				request.get(ServiceConstants.SCRT_MGR_ENDPOINT_KEY), request.get(ServiceConstants.REGION_KEY));
		// Create client-builder
		AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
		// Set end-point configuration to client-builder
		clientBuilder.setEndpointConfiguration(endpointConfig);
		// Create secrets manager client
		AWSSecretsManager secretsMgrClient = clientBuilder.build();

		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
				.withSecretId(request.get(ServiceConstants.SECRET_ID_KEY));
		// Fetch the secret value
		getSecretValueResult = secretsMgrClient.getSecretValue(getSecretValueRequest);

		if (getSecretValueResult == null) {
			return "Error occurred while retrieving the secret from secrets manager!";
		}

		return (getSecretValueResult.getSecretString() != null) ? getSecretValueResult.getSecretString()
				: getSecretValueResult.getSecretBinary();
	}

}