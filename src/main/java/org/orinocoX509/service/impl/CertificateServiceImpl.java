package org.orinocoX509.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import org.apache.velocity.app.VelocityEngine;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.entity.field.certificate.IssuerField;
import org.orinocoX509.entity.field.certificate.SubjectField;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.holder.CertificateInfo;
import org.orinocoX509.holder.CertificateValues;
import org.orinocoX509.service.CAKeyService;
import org.orinocoX509.service.CertificateProfileService;
import org.orinocoX509.service.CertificateService;
import org.orinocoX509.service.CertificateStatusService;
import org.orinocoX509.service.impl.extensions.AuthorityInformationAccessExtension;
import org.orinocoX509.service.impl.extensions.AuthorityKeyIdentifierExtension;
import org.orinocoX509.service.impl.extensions.CRLDistributionPointExtension;
import org.orinocoX509.service.impl.extensions.CertificatePoliciesExtension;
import org.orinocoX509.service.impl.extensions.DeviceTypeExtension;
import org.orinocoX509.service.impl.extensions.EngineExtension;
import org.orinocoX509.service.impl.extensions.ExtendedKeyUsageExtension;
import org.orinocoX509.service.impl.extensions.IssuerAlternativeNameExtension;
import org.orinocoX509.service.impl.extensions.KeyUsageExtension;
import org.orinocoX509.service.impl.extensions.NetscapeCertificateTypeExtension;
import org.orinocoX509.service.impl.extensions.SubjectAlternativeNameExtension;
import org.orinocoX509.service.impl.extensions.SubjectKeyIdentifierExtension;
import org.orinocoX509.service.impl.extensions.VelocitySupport;
import org.orinocoX509.util.CryptographicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl implements CertificateService 
{
	@Autowired
	private CertificateProfileService certificateProfileService;
	
	@Autowired
	private CertificateStatusService certificateStatusService;
	
	@Autowired
	private CAKeyService CAKeyService;
	
	@Autowired
	private VelocitySupport velocitySupport;
	
	@Autowired
	private CryptographicUtil cryptographicUtil;
	
	private VelocityEngine velocityEngine;
	
	private static final Logger log = LoggerFactory.getLogger(CertificateServiceImpl.class);
	private final Integer MAX_SERIAL_NUMBER_TRIES = 10;
	

	
	public CertificateServiceImpl()
	{
		Security.addProvider(new BouncyCastleProvider());
		this.velocityEngine = new VelocityEngine();
		velocityEngine.init();
	}
	
	private byte[] validatePublicKey(PublicKey publicKey, Integer keySize) throws EngineException
	{ 
		if ((publicKey instanceof RSAPublicKey) == true)
		{
			int publicKeySize = ((RSAPublicKey) publicKey).getModulus().bitLength();
			if (publicKeySize != keySize)
			{
				log.error("Key size mismatch. Expented: " + keySize + " Read: " + publicKeySize);
				throw new EngineException(EngineErrorCodes.INVALID_PUBLIC_KEY_SIZE, 
						"The public key has size (" + publicKeySize + ") but according to the profile it should be " + keySize);	
			}

			return(publicKey.getEncoded()); 
		}
		else
		{
			log.error("The public key has a wrong type (Non RSA). Algorithm:" + publicKey.getAlgorithm());
			throw new EngineException(EngineErrorCodes.INVALID_PUBLIC_KEY_TYPE, "The public key has a wrong type (Non RSA)");
		}
	}
	
	public CertificateInfo generateCertificate(CertificateProfile certificateProfile,CertificateValues certificateValues) throws EngineException
	{
		try
		{
			certificateProfileService.validateProfile(certificateProfile, certificateValues);
			
			PublicKey publicKey = cryptographicUtil.getPublicKey(certificateValues.getRequest());
			SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(
					(ASN1Sequence)new ASN1InputStream(
							new ByteArrayInputStream(validatePublicKey(publicKey, certificateProfile.getKeySize().getValue()))).readObject());
			
			X509v3CertificateBuilder certificateGenerator = new X509v3CertificateBuilder(
					new X500Name(getIssuer(certificateProfile)), 
					getSerialNumber(),
					getNotBefore(certificateProfile, certificateValues),
					getNotAfter(certificateProfile, certificateValues), 
					new X500Name(getSubject(certificateProfile, certificateValues)), 
					publicKeyInfo);
			
			certificateGenerator = setExtensions(certificateGenerator, certificateProfile, certificateValues, publicKey);
			
			
			ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider(CAKeyService.getProvider()).build(CAKeyService.getCAPrivateKey());
			X509CertificateHolder certificateHolder = certificateGenerator.build(contentSigner);
			
			CertificateInfo certificateInfo = new CertificateInfo();
			certificateInfo.setPemCertificate(cryptographicUtil.base64Encode(certificateHolder.getEncoded()));
			certificateInfo.setSerialNumber(certificateHolder.getSerialNumber());
			certificateInfo.setNotAfter(certificateHolder.getNotAfter());
			certificateInfo.setNotBefore(certificateHolder.getNotBefore());
			log.debug("Generated certificate with profile " + certificateProfile.getProfileName() + " successfully.\nValues:" + certificateValues.toString() + "\nCertificate: " + certificateInfo.toString());
			
			return(certificateInfo);
		}
		catch(OperatorCreationException exc)
		{
			log.error("Error generating the certificate." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.OPERATOR_CREATION_EXCEPTION, 
					"Error generating the certificate." + exc.getMessage() );
		}
		catch(IOException exc)
		{
			log.error("Error when creating the certificate." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.IO_ERROR, 
					"Error when creating the certificate." + exc.getMessage());
		}
	}
	
	
	
	private BigInteger getSerialNumber()
	{
		Integer currentTry = 0;
		BigInteger serialNumberCandidate;
		
		do
		{
			currentTry++;
			
			// 60 bits
			// 0 certainty
			serialNumberCandidate = new BigInteger(60, 0, new Random());
			log.debug("Checking if " + serialNumberCandidate + " is a valid serial number");
			
			CertificateStatus certificateStatus = new CertificateStatus(serialNumberCandidate);
			certificateStatus = certificateStatusService.getStatus(certificateStatus);
			if (certificateStatus.getCertificateStatus()  == CertificateStatusValues.U)
			{
				log.debug("Checking for " + serialNumberCandidate + " was valid");
				break;
			}
			
			log.debug("Checking for " + serialNumberCandidate + " was invalid (serial number is already used)");
		}
		while(currentTry < MAX_SERIAL_NUMBER_TRIES);
		
		if (currentTry >= MAX_SERIAL_NUMBER_TRIES)
		{
			log.error("Warning there is not serial numbers left!!! This error prevents from generate any certificate!!!");
			throw new EngineException(EngineErrorCodes.NO_SERIAL_NUMBERS_LEFT, "There is not serial numbers left");
		}
		
		return(serialNumberCandidate);
		
	}
	
	private Date getNotBefore(CertificateProfile certificateProfile,CertificateValues certificateValues)
	{
		if (certificateValues.getNotBefore() == null)
		{
			certificateValues.setNotBefore(new Date());
			log.debug("notBefore was not set for profile " + certificateProfile.toString());
		}
		return(certificateValues.getNotBefore());
	}
	
	private Date getNotAfter(CertificateProfile certificateProfile,CertificateValues certificateValues)
	{
		if (certificateValues.getNotAfter() == null)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.YEAR, certificateProfile.getYears());
			certificateValues.setNotAfter(calendar.getTime());
			log.debug("notAfter was not set for profile " + certificateProfile.toString());
		}
		return(certificateValues.getNotAfter());
	}
	
	private String getSubject(CertificateProfile certificateProfile, CertificateValues certificateValues)
	{
		try
		{
			String template = ((SubjectField) certificateProfile.getField(FieldType.SUBJECT)).getPattern();
			
			if (certificateValues != null)
			{
				Map<String,String> subjectValues = certificateValues.getValues().get(FieldType.SUBJECT);
				if (subjectValues != null)
				{
					return(velocitySupport.applyTemplate(template, subjectValues, "Subject template"));
				}
				else
				{
					log.error("No subject values were found for profile " + certificateProfile.getProfileName() + " and template " + template);
					return(template);
				}
			}
			else
			{
				log.error("certificateValues was null for subject " + certificateProfile.getProfileName() + " and template " + template);
				return(template);
			}
		}
		catch(Exception exc)
		{
			log.error("Subject is missing or invalid." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.INVALID_SUBJECT_EXTENSION, "Subject is missing or invalid." + exc.getMessage());
		}
	}
	
	private String getIssuer(CertificateProfile certificateProfile)
	{
		try
		{
			return(((IssuerField) certificateProfile.getField(FieldType.ISSUER)).getPattern());
		}
		catch(Exception exc)
		{
			log.error("Issuer is missing or invalid." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.INVALID_ISSUER_EXTENSION, "Issuer is missing or invalid." + exc.getMessage());
		}
	}
	

	
	private X509v3CertificateBuilder setExtensions(X509v3CertificateBuilder certificateGenerator, 
			CertificateProfile certificateProfile, 
			CertificateValues certificateValues,
			PublicKey publicKey)
	{
		
		EngineExtension extension = null;
		
		for(CertificateField currentField : certificateProfile.getFields())
		{
			log.debug("Adding field " + currentField.getFieldType().name() + " to profile " + certificateProfile.getProfileName());
			
			
			if (currentField.getFieldType() == FieldType.AUTHORITY_INFORMATION_ACCESS)
			{
				extension = new AuthorityInformationAccessExtension(currentField);
			}
			else if (currentField.getFieldType() == FieldType.AUTHORITY_KEY_IDENTIFIER)
			{
				extension = new AuthorityKeyIdentifierExtension(currentField, CAKeyService.getCAPublicKey(), CAKeyService.getCACertificate());
			}
			else if (currentField.getFieldType() == FieldType.CERTIFICATE_POLICY)
			{
				extension = new CertificatePoliciesExtension(currentField);
			}
			else if (currentField.getFieldType() == FieldType.CRL_DISTRIBUTION_POINT)
			{
				extension = new CRLDistributionPointExtension(currentField);
			}
			else if (currentField.getFieldType() == FieldType.DEVICE_TYPE)
			{
				extension = new DeviceTypeExtension(currentField);
			}
			else if (currentField.getFieldType() == FieldType.EXTENDED_KEY_USAGE)
			{
				extension = new ExtendedKeyUsageExtension(currentField);
			}
			else if (currentField.getFieldType() == FieldType.ISSUER_ALTERNATIVE_NAME)
			{
				extension = new IssuerAlternativeNameExtension(currentField);
			}
			if (currentField.getFieldType() == FieldType.KEY_USAGE)
			{
				extension = new KeyUsageExtension(currentField.getValues(), currentField.getCritical());
			}
			else if (currentField.getFieldType() == FieldType.NETSCAPE_CERTIFICATE_TYPE)
			{
				extension = new NetscapeCertificateTypeExtension(currentField);
			}
			else if (currentField.getFieldType() == FieldType.SUBJECT_ALTERNATIVE_NAME)
			{
				extension = new SubjectAlternativeNameExtension(currentField, certificateValues.getValues().get(FieldType.SUBJECT_ALTERNATIVE_NAME), velocitySupport);
			}
			else if (currentField.getFieldType() == FieldType.SUBJECT_KEY_IDENTIFIER)
			{
				extension = new SubjectKeyIdentifierExtension(currentField, publicKey);
			}
			
			if(extension != null)
			{
				try
				{
					certificateGenerator = extension.applyExtension(certificateGenerator);
				}
				catch(CertIOException exc)
				{
					log.error("IO error when adding the extension " + currentField.getFieldType().name() + 
							" from profile " + certificateProfile.getProfileName() + ".\n" +  
							"Details:\n" + exc.toString());	
					
					throw new EngineException(EngineErrorCodes.IO_ERROR_ADDING_EXTENSION, 
							"IO error when adding the extension " + currentField.getFieldType().name() + 
							" from profile " + certificateProfile.getProfileName() + ".\n" +  
							"Details:\n" + exc.toString());	
				}
			}
		}
		
		return(certificateGenerator);
	}
}
