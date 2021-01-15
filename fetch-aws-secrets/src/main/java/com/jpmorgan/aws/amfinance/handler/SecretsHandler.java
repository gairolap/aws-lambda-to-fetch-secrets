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
import com.amazonaws.util.StringUtils;
import com.jpmorgan.aws.amfinance.util.ServiceConstants;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecretsHandler implements RequestHandler<Map<String, String>, Object> {

	/**
	 * Method intercepts and processes the incoming requests.
	 */
	public Object handleRequest(Map<String, String> request, Context context) {

		return this.fetchSecretsFromSecertsMngr(request);
	}

	/**
	 * Fetch secret key and value for the given secret-id.
	 * 
	 * @param {@linkplain Map<String, String>}.
	 * @return {@linkplain Object}.
	 */
	private Object fetchSecretsFromSecertsMngr(Map<String, String> request) {

		String secretId = request.get(ServiceConstants.SECRET_ID.getConstVal());
		String region = request.get(ServiceConstants.REGION.getConstVal());
		String endpointHost = request.get(ServiceConstants.SECRET_MGR_HOST.getConstVal());
		GetSecretValueResult getSecretValueResult;

		if (StringUtils.isNullOrEmpty(secretId) || StringUtils.isNullOrEmpty(region)
				|| StringUtils.isNullOrEmpty(endpointHost)) {
			return ServiceConstants.EMPTY_OR_NULL_MANDATORY_PARAMS.getConstVal();
		}

		try {
			log.info("Retrieving the secret details for secret-id {} from region {}", secretId, region);

			// Create end-point configuration
			AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
					endpointHost, region);
			// Create client-builder
			AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
			// Set end-point configuration to client-builder
			clientBuilder.setEndpointConfiguration(endpointConfig);
			// Create secrets manager client
			AWSSecretsManager secretsMgrClient = clientBuilder.build();

			GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretId);
			// Fetch the secret value
			getSecretValueResult = secretsMgrClient.getSecretValue(getSecretValueRequest);

			if (getSecretValueResult == null) {
				return ServiceConstants.EMPTY_SECRET_DETAILS.getConstVal();
			}

			return (!StringUtils.isNullOrEmpty(getSecretValueResult.getSecretString()))
					? getSecretValueResult.getSecretString()
							: getSecretValueResult.getSecretBinary();
		} catch (Exception excp) {
			if (excp instanceof ResourceNotFoundException) {
				log.info(ServiceConstants.RES_NT_FOUND.getConstVal(), excp);
				return ServiceConstants.RES_NT_FOUND.getConstVal();
			} else if (excp instanceof InvalidRequestException) {
				log.info(ServiceConstants.INVLD_REQUEST.getConstVal(), excp);
				return ServiceConstants.INVLD_REQUEST.getConstVal();
			} else if (excp instanceof InvalidParameterException) {
				log.info(ServiceConstants.INVLD_REQUEST_PARAMS.getConstVal(), excp);
				return ServiceConstants.INVLD_REQUEST_PARAMS.getConstVal();
			} else if (excp instanceof AWSSecretsManagerException) {
				log.info(ServiceConstants.UNBL_TO_ACCESS_KEY_DTLS.getConstVal(), excp);
				return ServiceConstants.UNBL_TO_ACCESS_KEY_DTLS.getConstVal();
			} else {
				log.info(ServiceConstants.TECH_ERR.getConstVal(), excp);
				return ServiceConstants.TECH_ERR.getConstVal();
			}
		} finally {
			secretId = null;
			region = null;
			endpointHost = null;
			getSecretValueResult = null;
		}
	}

}