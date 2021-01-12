/**
 * Custom exception handler class for secrets manager.
 */
package com.jpmorgan.aws.amfinance.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.jpmorgan.aws.amfinance.util.ServiceConstants;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class SecretsExceptionHandler {

	@ExceptionHandler
	public final String handleException(Exception excp) {

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