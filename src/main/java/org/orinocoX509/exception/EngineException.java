package org.orinocoX509.exception;

public class EngineException extends RuntimeException 
{
	private static final long serialVersionUID = 4258143701939093139L;
	private EngineErrorCodes errorCode;
	private String message;
	
	public static enum EngineErrorCodes
	{
		// Profile
		DUPLICATE_PROFILE,
		DUPLICATE_FIELD_IN_PROFILE,
		
		// Certificate Generation
		INVALID_PUBLIC_KEY_SIZE,
		INVALID_PUBLIC_KEY_TYPE,
		INVALID_SUBJECT_EXTENSION,
		INVALID_ISSUER_EXTENSION,
		UNKNOWN_CRYPTOGRAPHIC_PROVIDER,
		UNKNOWN_KEYSTORE_TYPE,
		KEY_NOT_FOUND,
		CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED,
		COULD_NOT_BE_INITIALIZED_THE_KEYSTORE,
		IO_ERROR,
		OPERATOR_CREATION_EXCEPTION,
		PKCS10_REQUEST_MISSING,
		WRONG_PKCS10_FORMAT,
		IO_ERROR_ADDING_EXTENSION,
		PROFILE_MALFORMED,
		WRONG_CERTIFICATE_VALUES,
		NO_SERIAL_NUMBERS_LEFT,
		
		// CRL Generation
		GENERATION_CRL_ERROR,
		COULD_NOT_BE_CREATED_CRL_FACTORY,
		
		// OCSP 
		OCSP_EXCEPTION,
		WRONG_ENCODING_FORMAT,
		NO_OCSP_URL_AVAILABLE,
		
		// General
		CONNECTION_REFUSED,
		WRONG_URL,
		
		// Certificate Field Extractor
		NO_CRL_DP_URL_AVAILABLE;
		
	}

	public EngineException(EngineErrorCodes errorCode, String message)
	{
		this.errorCode = errorCode;
		this.message = message;
	}

	public EngineErrorCodes getErrorCode() 
	{
		return(this.errorCode);
	}

	public String getMessage() 
	{
		return(this.message);
	}
	
}
