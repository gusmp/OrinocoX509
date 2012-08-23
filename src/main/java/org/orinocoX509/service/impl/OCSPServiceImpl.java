package org.orinocoX509.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Random;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.HttpService;
import org.orinocoX509.service.OCSPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OCSPServiceImpl implements OCSPService 
{

	@Autowired
	private HttpService httpService;
	
	@Autowired
	private CertificateFieldExtractorServiceImpl certificateFieldExtractorService;
	
	private static final Logger log = LoggerFactory.getLogger(OCSPServiceImpl.class);
	private String providerName;
	
	public OCSPServiceImpl()
	{
		this("BC");
	}
	
	public OCSPServiceImpl(String providerName)
	{
		this.providerName = providerName;
		if (Security.getProvider("BC") == null)
		{
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	

	
	public CertificateStatusValues getStatus(X509Certificate issuer, BigInteger serialNumber) throws EngineException 
	{
		// get OCSP url
		String ocsp_url =  certificateFieldExtractorService.getOcspUrl(issuer);

		return (getStatus(issuer, serialNumber, ocsp_url));
	}
	
	public CertificateStatusValues getStatus(X509Certificate issuer, BigInteger serialNumber, String urlOcsp) throws EngineException
	{
		try
		{
			log.debug("Get OCSP status for " +  serialNumber);
			
			// create OCSP request
			X509CertificateHolder certificateHolder = new X509CertificateHolder(issuer.getEncoded());
			
			DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().setProvider(providerName).build();
			CertificateID certificateId = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), certificateHolder, serialNumber);
			
			OCSPReqBuilder ocspRequestBuilder = new OCSPReqBuilder();
			ocspRequestBuilder.addRequest(certificateId);
			
			// add nonce
			byte[] sampleNonce = new byte[16];
	        Random rand = new Random();
	        rand.nextBytes(sampleNonce);
			ExtensionsGenerator extGen = new ExtensionsGenerator();
	        extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(sampleNonce));
	        ocspRequestBuilder.setRequestExtensions(extGen.generate());
	        log.debug("None OK for " + serialNumber);
			
			// add requestor name
	        ocspRequestBuilder.setRequestorName(new GeneralName(GeneralName.directoryName, new X509Principal(issuer.getSubjectDN().getName())));
			OCSPReq ocspRequest = ocspRequestBuilder.build();
			log.debug("Add requestor name successfully for " + serialNumber);
			
			// send OCSP request
			byte[] bOcspResponse = httpService.sendData(ocspRequest.getEncoded(), "application/ocsp-request", urlOcsp);
			
			OCSPResp ocspResponse = new OCSPResp(bOcspResponse);
			log.debug("OCSP status for " + serialNumber + " " + ocspResponse.getStatus());
			
			/*
			public static final int SUCCESSFUL = 0;
		    public static final int MALFORMED_REQUEST = 1;
		    public static final int INTERNAL_ERROR = 2;
		    public static final int TRY_LATER = 3;
		    public static final int SIG_REQUIRED = 5;
		    public static final int UNAUTHORIZED = 6;
		    */
			
			if (ocspResponse.getStatus() ==  OCSPResponseStatus.SUCCESSFUL)
			{
				BasicOCSPResp resp = (BasicOCSPResp) ocspResponse.getResponseObject();
				
				if (resp.getResponses()[0].getCertStatus() == CertificateStatus.GOOD)
				{
					log.debug("Status of " + serialNumber + " is GOOD");
					return(CertificateStatusValues.V);
				}
				else if (resp.getResponses()[0].getCertStatus() instanceof RevokedStatus) 
				{
					log.debug("Status of " + serialNumber + " is REVOKED");
					return(CertificateStatusValues.R);
				}
				else if (resp.getResponses()[0].getCertStatus() instanceof UnknownStatus)
				{
					log.debug("Status of " + serialNumber + " is UNKNOWN");
					return(CertificateStatusValues.U);
				}
			}
			else
			{
				log.error("OCSP status for " + serialNumber + " was " + ocspResponse.getStatus() + "!!!");
			}
			
			return(CertificateStatusValues.U);
		}
		catch(IOException exc)
		{
			log.error("Error when creating OCSP request." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.IO_ERROR, "Error when creating the certificate." + exc.getMessage());
		}
		catch(OperatorCreationException exc)
		{
			log.error("Error generating OCSP request." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.OPERATOR_CREATION_EXCEPTION, "Error generating OCSP request." + exc.getMessage());
		}
		catch(OCSPException exc)
		{
			log.error("Error creating CertificateID object.\n" + exc.toString());
			throw new EngineException(EngineErrorCodes.OCSP_EXCEPTION, "Error creating CertificateID object.\n" + exc.toString());
		}
		catch(CertificateEncodingException exc)
		{
			log.error("The certificate to test has an unknown encoding format.\n" + exc.toString());
			throw new EngineException(EngineErrorCodes.WRONG_ENCODING_FORMAT, "The certificate to test has an unknown encoding format.\n" + exc.toString());
		}
	}

}
