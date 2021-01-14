/**
 * AWS Handler class for fetching secrets from AWS secrets manager.
 */
package com.jpmorgan.aws.amfinance.handler;

import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.jpmorgan.aws.amfinance.util.ServiceConstants;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SecretsHandler implements RequestHandler<Map<String, String>, Object> {

	/**
	 * Method intercepts and processes the incoming requests.
	 */
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

		try {
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
		} catch (Exception excp) {
			if (excp instanceof ResourceNotFoundException) {
				log.info(ServiceConstants.RES_NT_FOUND, excp);
				return ServiceConstants.RES_NT_FOUND;
			} else if (excp instanceof InvalidRequestException) {
				log.info(ServiceConstants.INVLD_REQUEST, excp);
				return ServiceConstants.INVLD_REQUEST;
			} else if (excp instanceof InvalidParameterException) {
				log.info(ServiceConstants.INVLD_REQUEST_PARAMS, excp);
				return ServiceConstants.INVLD_REQUEST_PARAMS;
			} else if (excp instanceof AWSSecretsManagerException) {
				log.info(ServiceConstants.UNBL_TO_ACCESS_KEY_DTLS, excp);
				return ServiceConstants.UNBL_TO_ACCESS_KEY_DTLS;
			} else {
				log.info(ServiceConstants.TECH_ERR, excp);
				return ServiceConstants.TECH_ERR;
			}
		}
	}

}