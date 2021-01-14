/**
 * Class to hold service constants.
 */
package com.jpmorgan.aws.amfinance.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceConstants {

	REGION("region"), SECRET_MGR_HOST("secretMgrHost"), SECRET_ID("secretId"),
	RES_NT_FOUND("Requested secret isn't found!"), INVLD_REQUEST("Invalid request received!"),
	INVLD_REQUEST_PARAMS("Request had invalid params!"), UNBL_TO_ACCESS_KEY_DTLS("Unable to access the secret!"),
	TECH_ERR("Technical error occurred while retrieving the secret!"),
	EMPTY_SECRET_DETAILS("Error occurred while retrieving the secret from secrets manager!"),
	EMPTY_OR_NULL_MANDATORY_PARAMS("Either of the mandatory request parameters is NULL or EMPTY");

	String constVal;

	private ServiceConstants(String value) {

		this.constVal = value;
	}

}