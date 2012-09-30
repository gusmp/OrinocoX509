package org.orinocoX509.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.field.certificate.AuthorityInformationAccessField;
import org.orinocoX509.entity.field.certificate.AuthorityKeyIdentifierField;
import org.orinocoX509.entity.field.certificate.CRLDistributionPointField;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.entity.field.certificate.CertificatePolicyField;
import org.orinocoX509.entity.field.certificate.DeviceTypeField;
import org.orinocoX509.entity.field.certificate.ExtendedKeyUsageField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.entity.field.certificate.IssuerAlternativeNameField;
import org.orinocoX509.entity.field.certificate.IssuerField;
import org.orinocoX509.entity.field.certificate.KeyUsageField;
import org.orinocoX509.entity.field.certificate.NetscapeCertificateTypeField;
import org.orinocoX509.entity.field.certificate.QCStatementField;
import org.orinocoX509.entity.field.certificate.SubjectAlternativeNameField;
import org.orinocoX509.entity.field.certificate.SubjectDirectoryAttributeField;
import org.orinocoX509.entity.field.certificate.SubjectField;
import org.orinocoX509.entity.field.certificate.SubjectKeyIdentifierField;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue;
import org.orinocoX509.entity.value.certificate.CRLDistributionPointFieldValue;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.IssuerAlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.IssuerFieldValue;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue.QCStatementType;
import org.orinocoX509.entity.value.certificate.SubjectAlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue.SubjectDirectoryAttributeType;
import org.orinocoX509.entity.value.certificate.SubjectFieldValue;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue.AlternativeNameType;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue.AIAType;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue.AuthorityKeyIdentifierType;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue.CertificatePolicyType;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue.ExtendedKeyUsageType;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue.KeyUsageType;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue.NetscapeCertificateTypeType;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CertificateProfileServiceTest 
{
	@Autowired
	CertificateProfileService certificateProfileService;
	
	@Autowired
	TestSupport testSupport;
	
	private CertificateProfile profile = null;

	
	@Before
	public void init() { }
	
	@Test
	public void addProfileTest()
	{
		profile = testSupport.createEmptyProfile("addProfileTest",TestConst.PROFILE_DESCRIPTION);
		profile = certificateProfileService.saveProfile(profile);
		assertNotNull(certificateProfileService.getProfile(profile));
	}
	
	@Test
	public void deleteProfileTest()
	{
		profile = testSupport.createEmptyProfile("deleteProfileTest",TestConst.PROFILE_DESCRIPTION);
		profile = certificateProfileService.saveProfile(profile);
		certificateProfileService.deleteProfile(profile);
			
		profile.setVersion(null);
		assertNull(certificateProfileService.getProfile(profile));
	}
	
	@Test
	public void getProfileTest()
	{
		final String PROFILE_NAME = "getProfileTest";
		
		profile = testSupport.createEmptyProfile(PROFILE_NAME,TestConst.PROFILE_DESCRIPTION);
		profile = certificateProfileService.saveProfile(profile);
		profile = certificateProfileService.getProfile(profile);
		assertEquals(PROFILE_NAME, profile.getProfileName());
		assertEquals(TestConst.PROFILE_DESCRIPTION, profile.getProfileDescription());
		assertEquals(TestConst.PROFILE_KEY_SIZE, profile.getKeySize());
		assertEquals(TestConst.PROFILE_YEARS, profile.getYears());
	}
	
	@Test
	public void addDoubleFieldTest()
	{
		try
		{
			profile = testSupport.createEmptyProfile("addDoubleProfileTest",TestConst.PROFILE_DESCRIPTION);
			profile = certificateProfileService.saveProfile(profile);
			profile.addField(new SubjectField(profile, "cn=subject"));
			profile.addField(new SubjectField(profile, "cn=subject"));
			fail();
		}
		catch(EngineException exc)
		{
			assertEquals(EngineErrorCodes.DUPLICATE_FIELD_IN_PROFILE, exc.getErrorCode());
		}
	}
	
	@Test
	public void addDoubleProfileTest()
	{
		String PROFILE_NAME= "addDoubleProfileTest";
		try
		{
			profile = testSupport.createEmptyProfile(PROFILE_NAME,TestConst.PROFILE_DESCRIPTION);
			profile = certificateProfileService.saveProfile(profile);
			CertificateProfile profile2 = testSupport.createEmptyProfile(PROFILE_NAME,TestConst.PROFILE_DESCRIPTION);
			profile2 = certificateProfileService.saveProfile(profile2);
			fail();
		}
		catch(EngineException exc)
		{
			assertEquals(EngineErrorCodes.DUPLICATE_PROFILE, exc.getErrorCode());
		}
	}
	
	// Subject
	
	private void testSubjectIssuer(String profileName, String pattern, String cn_key, String cn_value, String o_key, 
			String o_value, boolean isSubject)
	{
		
		profile = testSupport.createEmptyProfile(profileName,TestConst.PROFILE_DESCRIPTION);
		CertificateField field2Test;
		
		if (isSubject == true)
		{
			field2Test = new SubjectField(profile, pattern);
			field2Test.addValue(new SubjectFieldValue(cn_key, cn_value));
			field2Test.addValue(new SubjectFieldValue(o_key, o_value));
		}
		else
		{
			field2Test = new IssuerField(profile, pattern);
			field2Test.addValue(new IssuerFieldValue(cn_key, cn_value));
			field2Test.addValue(new IssuerFieldValue(o_key, o_value));
		}
		
		profile.addField(field2Test);
		profile = certificateProfileService.saveProfile(profile);
		
		CertificateProfile profile2 = new CertificateProfile();
		profile2.setProfileId(profile.getProfileId());
		profile2 = certificateProfileService.getProfile(profile2);
		if (isSubject == true)
		{
			field2Test = profile2.getField(FieldType.SUBJECT);
		}
		else
		{
			field2Test = profile2.getField(FieldType.ISSUER);
		}
		assertNotNull(field2Test);
		
		assertEquals(2, field2Test.getValues().size());
		testSupport.checkFieldValue(field2Test.getValues(), Arrays.asList(cn_key, o_key), true, isSubject);
		testSupport.checkFieldValue(field2Test.getValues(), Arrays.asList(cn_value, o_value), false, isSubject);
	}
	
	@Test
	public void addSubjectFieldTest()
	{
		String SUBJECT_PATTERN = "CN=$cn,O=$o";
		String CN_KEY = "$cn";
		String CN_VALUE = "cn_subject";
		String O_KEY = "$o";
		String O_VALUE = "o_subject";
		
		testSubjectIssuer("addSubjectFieldTest", SUBJECT_PATTERN, CN_KEY, CN_VALUE, O_KEY, O_VALUE, true);
	}
	
	// Issuer
	
	@Test
	public void addIssuerFieldTest()
	{
		String ISSUER_PATTERN = "CN=$cn,O=$o";
		String CN_KEY = "$cn";
		String CN_VALUE = "cn_issuer";
		String O_KEY = "$o";
		String O_VALUE = "o_issuer";
		
		testSubjectIssuer("addIssuerFieldTest", ISSUER_PATTERN, CN_KEY, CN_VALUE, O_KEY, O_VALUE, false);
	}
	
	// Authority Information Access
	
	@Test
	public void addAIAFieldTest()
	{

		Boolean CRITICAL = false;
		
		profile = testSupport.createEmptyProfile("addAIAFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField authorityInformationAccess = new AuthorityInformationAccessField(profile, CRITICAL);
		authorityInformationAccess.addValue(new AuthorityInformationAccessFieldValue(AIAType.ID_AD_OCSP,TestConst.AIA_OCSP_URL));
		authorityInformationAccess.addValue(new AuthorityInformationAccessFieldValue(AIAType.ID_AD_CA_ISSUERS,TestConst.AIA_CA_URL));
		profile.addField(authorityInformationAccess);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		authorityInformationAccess = profile.getField(FieldType.AUTHORITY_INFORMATION_ACCESS);
		assertEquals(CRITICAL, authorityInformationAccess.getCritical());
		assertEquals(2, authorityInformationAccess.getValues().size());
		
		for(int i=0; i < authorityInformationAccess.getValues().size(); i++)
		{
			AuthorityInformationAccessFieldValue aiaFieldValue = (AuthorityInformationAccessFieldValue) authorityInformationAccess.getValues().get(i);
			
			if (aiaFieldValue.getAiaType() == AIAType.ID_AD_OCSP)
			{
				assertEquals(TestConst.AIA_OCSP_URL, aiaFieldValue.getAiaValue());
				continue;
			}
			else if (aiaFieldValue.getAiaType() == AIAType.ID_AD_CA_ISSUERS)
			{
				assertEquals(TestConst.AIA_CA_URL, aiaFieldValue.getAiaValue());
				continue;
			}
			fail();
			
		}
	}
	
	
	// Authority Key Identifier
	
	@Test
	public void addAuthorityKeyIdentifierFieldTest()
	{
		Boolean CRITICAL = false;
		profile = testSupport.createEmptyProfile("addAuthorityKeyIdentifierFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField authorityKeyIdentifier = new AuthorityKeyIdentifierField(profile, CRITICAL);
		authorityKeyIdentifier.addValue(new AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType.KEY_IDENTIFIER));
		authorityKeyIdentifier.addValue(new AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType.AUTH_CERT_ISSUER_AUTH_CERT_SERIAL_NUMBER));
		profile.addField(authorityKeyIdentifier);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		authorityKeyIdentifier = profile.getField(FieldType.AUTHORITY_KEY_IDENTIFIER);
		assertEquals(CRITICAL, authorityKeyIdentifier.getCritical());
		assertEquals(2, authorityKeyIdentifier.getValues().size());
		
		for(int i=0; i < authorityKeyIdentifier.getValues().size(); i++)
		{
			AuthorityKeyIdentifierFieldValue aiaFieldValue = (AuthorityKeyIdentifierFieldValue) authorityKeyIdentifier.getValues().get(i);
			
			if (aiaFieldValue.getAuthorityKeyIdentifier()  == AuthorityKeyIdentifierType.KEY_IDENTIFIER)
			{
				continue;
			}
			else if (aiaFieldValue.getAuthorityKeyIdentifier() == AuthorityKeyIdentifierType.AUTH_CERT_ISSUER_AUTH_CERT_SERIAL_NUMBER)
			{
				continue;
			}
			fail();
		}
	}
	
	
	// Certificate Policies
	
	@Test
	public void addCertificatePolicyFieldTest()
	{

		Boolean CRITICAL = true;
		
		profile = testSupport.createEmptyProfile("addCertificatePolicyFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField certificatePolicy = new CertificatePolicyField(profile, TestConst.POLICY_OID, CRITICAL);
		certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.CPS,TestConst.CPS ));
		certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.USER_NOTICE,TestConst.USER_NOTICE ));
		profile.addField(certificatePolicy);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		certificatePolicy = profile.getField(FieldType.CERTIFICATE_POLICY);
		assertEquals(TestConst.POLICY_OID, ((CertificatePolicyField) certificatePolicy).getPolicyOid());
		assertEquals(CRITICAL, ((CertificatePolicyField) certificatePolicy).getCritical());
		assertEquals(2, certificatePolicy.getValues().size());
		
		for(int i=0; i < certificatePolicy.getValues().size(); i++)
		{
			CertificatePolicyFieldValue certificateFieldValue = ((CertificatePolicyFieldValue) certificatePolicy.getValues().get(i)); 
			if (certificateFieldValue.getCertificatePolicyType() == CertificatePolicyType.CPS)
			{
				assertEquals(TestConst.CPS, certificateFieldValue.getCertificatePolicy());
				continue;
			}
			else if (certificateFieldValue.getCertificatePolicyType() == CertificatePolicyType.USER_NOTICE)
			{
				assertEquals(TestConst.USER_NOTICE, certificateFieldValue.getCertificatePolicy());
				continue;
			}
			
			fail();
		}
	}
	
	// CRL Distribution Point
	
	@Test
	public void addCRLDistributionPointFieldTest()
	{
		Boolean CRITICAL = false;
		profile = testSupport.createEmptyProfile("addCRLDistributionPointFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField crlDistributionPoint = new CRLDistributionPointField(profile, CRITICAL);
		crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP1_URL1, TestConst.CRLDP1_URL2)));
		crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP2_URL1, TestConst.CRLDP2_URL2)));
		profile.addField(crlDistributionPoint);
		
		certificateProfileService.saveProfile(profile);
		crlDistributionPoint = profile.getField(FieldType.CRL_DISTRIBUTION_POINT);
		assertEquals(2, crlDistributionPoint.getValues().size());
		assertEquals(CRITICAL, crlDistributionPoint.getCritical());
		
		for(int i=0; i < crlDistributionPoint.getValues().size(); i++)
		{
			CRLDistributionPointFieldValue crlDpFieldValue = (CRLDistributionPointFieldValue) crlDistributionPoint.getValues().get(i);

			if ((crlDpFieldValue.getValues().get(0).equalsIgnoreCase(TestConst.CRLDP1_URL1) == true) &&
					(crlDpFieldValue.getValues().get(1).equalsIgnoreCase(TestConst.CRLDP1_URL2) == true))
			{
				continue;
			}
			else if ((crlDpFieldValue.getValues().get(0).equalsIgnoreCase(TestConst.CRLDP2_URL1) == true) &&
					(crlDpFieldValue.getValues().get(1).equalsIgnoreCase(TestConst.CRLDP2_URL2) == true))
			{
				continue;
			}
				
			fail();
		}
	}
	
	// Device Type
	
	@Test
	public void addDeviceTypeFieldTest()
	{
		Boolean CRITICAL = false;
		profile = testSupport.createEmptyProfile("addDeviceTypeFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField deviceType = new DeviceTypeField(profile, TestConst.DEVICE_TYPE_TEXT, CRITICAL);
		profile.addField(deviceType);
		certificateProfileService.saveProfile(profile);
		deviceType = profile.getField(FieldType.DEVICE_TYPE);
		assertNotNull(deviceType);
		assertEquals(TestConst.DEVICE_TYPE_TEXT, ((DeviceTypeField) deviceType).getDeviceType());
	}
	
	// Extended Key Usage
	
	@Test
	public void addExtendedKeyUsageFieldTest()
	{
		Boolean CRITICAL = true;
		profile = testSupport.createEmptyProfile("addExtendedKeyUsageFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField extendedKeyUsageField = new ExtendedKeyUsageField(profile, CRITICAL);
		extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_CODE_SIGNING));	
		extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_IPSEC_IKE));
		profile.addField(extendedKeyUsageField);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		extendedKeyUsageField = profile.getField(FieldType.EXTENDED_KEY_USAGE);
		
		assertEquals(CRITICAL, extendedKeyUsageField.getCritical());
		for(int i=0; i < extendedKeyUsageField.getValues().size(); i++)
		{
			if ((((ExtendedKeyUsageFieldValue) extendedKeyUsageField.getValues().get(i)).getExtendedKeyUsage() != ExtendedKeyUsageType.ID_KP_CODE_SIGNING) &&
				(((ExtendedKeyUsageFieldValue) extendedKeyUsageField.getValues().get(i)).getExtendedKeyUsage() != ExtendedKeyUsageType.ID_KP_IPSEC_IKE))
			{
				fail();
			}
		}
	}
	
	// Issuer Alternative Name
	
	@Test
	public void addIssuerAlternativeNameFieldTest()
	{
		Boolean CRITICAL = true;

		
		profile = testSupport.createEmptyProfile("addIssuerAlternativeNameFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField issuerAlternativeName = new IssuerAlternativeNameField(profile, CRITICAL);
		issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.RFC822NAME, TestConst.IAN_RFC822NAME_VALUE));
		issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.DIRECTORY_NAME, TestConst.IAN_DIRECTORY_NAME_VALUE));
		profile.addField(issuerAlternativeName);
		
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		issuerAlternativeName = profile.getField(FieldType.ISSUER_ALTERNATIVE_NAME);
		assertEquals(CRITICAL, issuerAlternativeName.getCritical());
		for(int i=0; i < issuerAlternativeName.getValues().size(); i++)
		{
			IssuerAlternativeNameFieldValue issuerAlternativeNameFieldValue = (IssuerAlternativeNameFieldValue) issuerAlternativeName.getValues().get(i);
			if ((issuerAlternativeNameFieldValue.getAlternativeNameType() == AlternativeNameType.RFC822NAME) &&
					(issuerAlternativeNameFieldValue.getValue().equalsIgnoreCase(TestConst.IAN_RFC822NAME_VALUE) == true))
			{
				continue;
			}
			
			if ((issuerAlternativeNameFieldValue.getAlternativeNameType() == AlternativeNameType.DIRECTORY_NAME) &&
					(issuerAlternativeNameFieldValue.getValue().equalsIgnoreCase(TestConst.IAN_DIRECTORY_NAME_VALUE) == true))
			{
				continue;
			}
			
			fail();
		}
	}
	
	// Key Usage
	
	@Test
	public void addKeyUsageFieldTest()
	{
		
		Boolean CRITICAL = true;
		profile = testSupport.createEmptyProfile("addKeyUsageFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField keyUsageField = new KeyUsageField(profile, CRITICAL);
		keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
		keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
		profile.addField(keyUsageField);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		keyUsageField = profile.getField(FieldType.KEY_USAGE);
		
		assertEquals(CRITICAL, keyUsageField.getCritical());
		for(int i=0; i < keyUsageField.getValues().size(); i++)
		{
			if ((((KeyUsageFieldValue) keyUsageField.getValues().get(i)).getKeyUsage() != KeyUsageType.CRL_SIGN) &&
				(((KeyUsageFieldValue) keyUsageField.getValues().get(i)).getKeyUsage() != KeyUsageType.DIGITAL_SIGNATURE))
			{
				fail();
			}
		}
	}
	
	// Netscape Certificate Type
	
	@Test
	public void addNetscapeCertificateTypeFieldTest()
	{
		
		Boolean CRITICAL = true;
		profile = testSupport.createEmptyProfile("addNetscapeCertificateTypeFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField netscapeCertificateTypeField = new NetscapeCertificateTypeField(profile, CRITICAL);
		netscapeCertificateTypeField.addValue(new NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType.OBJECT_SIGNING));
		netscapeCertificateTypeField.addValue(new NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType.SMIME));
		profile.addField(netscapeCertificateTypeField);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		netscapeCertificateTypeField = profile.getField(FieldType.NETSCAPE_CERTIFICATE_TYPE);
		
		assertEquals(CRITICAL, netscapeCertificateTypeField.getCritical());
		for(int i=0; i < netscapeCertificateTypeField.getValues().size(); i++)
		{
			if ((((NetscapeCertificateTypeFieldValue) netscapeCertificateTypeField.getValues().get(i)).getNetscapeCertificateType() != NetscapeCertificateTypeType.OBJECT_SIGNING) &&
				(((NetscapeCertificateTypeFieldValue) netscapeCertificateTypeField.getValues().get(i)).getNetscapeCertificateType() != NetscapeCertificateTypeType.SMIME))
			{
				fail();
			}
		}
	}
	
	// Qualified Certificate Statements
	
	@Test
	public void addQualifiedCertificateStatementsTest()
	{
		Boolean CRITICAL = false;
		String CURRENCY_CODE = "EUR";
		Integer VALUE = 100;
		
		profile = testSupport.createEmptyProfile("addQCStatementsTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField qcStatements = new QCStatementField(profile, CRITICAL);
		qcStatements.addValue(new QCStatementFieldValue(QCStatementType.ID_QCS_PKIXQCSYNTAX_V1));
		qcStatements.addValue(new QCStatementFieldValue(QCStatementType.ID_ETSI_QCS_LIMITE_VALUE,CURRENCY_CODE, VALUE));
		
		profile.addField(qcStatements);
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		qcStatements = profile.getField(FieldType.QUALIFIED_CERTIFICATE_STATEMENT);
		
		assertEquals(CRITICAL, qcStatements.getCritical());
		for(int i=0; i < qcStatements.getValues().size(); i++)
		{
			QCStatementFieldValue qcsValue = (QCStatementFieldValue) qcStatements.getValues().get(i);
			if ((qcsValue.getQCStatement() != QCStatementType.ID_QCS_PKIXQCSYNTAX_V1) && 
					(qcsValue.getQCStatement() != QCStatementType.ID_ETSI_QCS_LIMITE_VALUE))
			{
				fail();
			}
			else
			{
				if (qcsValue.getQCStatement() == QCStatementType.ID_ETSI_QCS_LIMITE_VALUE)
				{
					assertEquals(CURRENCY_CODE, qcsValue.getCurrencyCode());
					assertEquals(VALUE, qcsValue.getLimiteValue());
				}
			}
		}
	}
	
	// Subject Alternative Name
	
	@Test
	public void addSubjectAlternativeNameFieldTest()
	{
		Boolean CRITICAL = false;
		String RFC822NAME_VALUE = "mail@subject.com";
		String DIRECTORY_NAME_VALUE = "SERIALNUMBER=$sn, CN=$cn";
		
		profile = testSupport.createEmptyProfile("addSubjectAlternativeNameFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField subjectAlternativeName = new SubjectAlternativeNameField(profile, CRITICAL);
		subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.RFC822NAME, RFC822NAME_VALUE));
		subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.DIRECTORY_NAME, DIRECTORY_NAME_VALUE));
		profile.addField(subjectAlternativeName);
		
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		subjectAlternativeName = profile.getField(FieldType.SUBJECT_ALTERNATIVE_NAME);
		assertEquals(CRITICAL, subjectAlternativeName.getCritical());
		for(int i=0; i < subjectAlternativeName.getValues().size(); i++)
		{
			SubjectAlternativeNameFieldValue subjectAlternativeNameFieldValue = (SubjectAlternativeNameFieldValue) subjectAlternativeName.getValues().get(i);
			if ((subjectAlternativeNameFieldValue.getAlternativeNameType() == AlternativeNameType.RFC822NAME) &&
					(subjectAlternativeNameFieldValue.getValue().equalsIgnoreCase(RFC822NAME_VALUE) == true))
			{
				continue;
			}
			
			if ((subjectAlternativeNameFieldValue.getAlternativeNameType() == AlternativeNameType.DIRECTORY_NAME) &&
					(subjectAlternativeNameFieldValue.getValue().equalsIgnoreCase(DIRECTORY_NAME_VALUE) == true))
			{
				continue;
			}
			
			fail();
		}
	}
	
	// Subject directory attributes
	
	@Test
	public void addSubjectDirectoryAttributesFieldTest()
	{
		Boolean CRITICAL = false;
		String RESIDENCE_COUNTRY_CODE = "ES";
		String NATIVE_COUNTRY_CODE = "UK";
		profile = testSupport.createEmptyProfile("addSubjectDirectoryAttributesFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField subjectDirectoryAttributes = new SubjectDirectoryAttributeField(profile, CRITICAL);
		subjectDirectoryAttributes.addValue(new SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP, RESIDENCE_COUNTRY_CODE));
		subjectDirectoryAttributes.addValue(new SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE, NATIVE_COUNTRY_CODE));
		profile.addField(subjectDirectoryAttributes);
		
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		subjectDirectoryAttributes = profile.getField(FieldType.SUBJECT_DIRECTORY_ATTRIBUTE);
		
		for(int i=0; i < subjectDirectoryAttributes.getValues().size(); i++)
		{
			SubjectDirectoryAttributeFieldValue sdaFieldValue = (SubjectDirectoryAttributeFieldValue) subjectDirectoryAttributes.getValues().get(i);
			if (sdaFieldValue.getSubjectDirectoryAttribute() == SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP)
			{
				assertEquals(RESIDENCE_COUNTRY_CODE, sdaFieldValue.getValue());
			}
			else if (sdaFieldValue.getSubjectDirectoryAttribute() == SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE)
			{
				assertEquals(NATIVE_COUNTRY_CODE, sdaFieldValue.getValue());
			}
			else
			{
				fail();
			}
		}
	}

	
	// Subject Key Identifier
	
	@Test
	public void addSubjectKeyIdentifierFieldTest()
	{
		Boolean CRITICAL = false;
		
		profile = testSupport.createEmptyProfile("addSubjectKeyIdentifierFieldTest",TestConst.PROFILE_DESCRIPTION);
		CertificateField subjectKeyIdentifier = new SubjectKeyIdentifierField(profile, CRITICAL);
		profile.addField(subjectKeyIdentifier);
		
		certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		subjectKeyIdentifier = profile.getField(FieldType.SUBJECT_KEY_IDENTIFIER);
		assertNotNull(subjectKeyIdentifier);
		assertEquals(CRITICAL, subjectKeyIdentifier.getCritical());
	}
	
	//
	

	
	@Test
	public void addTwoFieldsFieldTest()
	{
		CertificateProfile profile = testSupport.createEmptyProfile("addTwoFieldsFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField subjectField = new SubjectField(profile, "CN=subject");
		profile.addField(subjectField);
		
		CertificateField keyUsageField = new KeyUsageField(profile, true);
		profile.addField(keyUsageField);
		
		certificateProfileService.saveProfile(profile);
		
		CertificateProfile profile2 = new CertificateProfile();
		profile2.setProfileId(profile.getProfileId());
		profile2 = certificateProfileService.getProfile(profile2);
		assertEquals(2, profile2.getFields().size());
		certificateProfileService.deleteProfile(profile2);
	}
	
	@Test
	public void deleteFieldTest()
	{
		profile = testSupport.createEmptyProfile("deleteFieldTest",TestConst.PROFILE_DESCRIPTION);
		
		CertificateField subjectField = new SubjectField(profile, "CN=subject");
		profile.addField(subjectField);
		
		KeyUsageField keyUsageField = new KeyUsageField(profile, true);
		keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
		keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
		profile.addField(keyUsageField);
		
		profile = certificateProfileService.saveProfile(profile);
		
		profile.getFields().remove(profile.getField(FieldType.SUBJECT));
		profile = certificateProfileService.saveProfile(profile);
		
		profile = certificateProfileService.getProfile(profile);
		assertEquals(1, profile.getFields().size());
		
		keyUsageField = (KeyUsageField) profile.getField(FieldType.KEY_USAGE);
		assertEquals(2, keyUsageField.getValues().size());
	}
	
	@Test
	public void getAllprofiles()
	{
		profile = testSupport.createEmptyProfile("getAllprofiles1",TestConst.PROFILE_DESCRIPTION);
		profile = certificateProfileService.saveProfile(profile);
		CertificateProfile profile2 = testSupport.createEmptyProfile("getAllprofiles2",TestConst.PROFILE_DESCRIPTION);
		profile2 = certificateProfileService.saveProfile(profile2);
		
		List<CertificateProfile> profileList = certificateProfileService.getProfiles();
		assertNotNull(profileList);
		assertEquals(2, profileList.size());
		certificateProfileService.deleteProfile(profile2);
		
	}
	
	
	/*
	@Test
	public void concurrentTest()
	{
		
		try
		{
			profile = createProfile("concurrentTest",PROFILE_DESCRIPTION);
			
			CertificateField subjectField = new SubjectField(profile, "CN=subject");
			profile.addField(subjectField);
			
			profile = certificateProfileService.saveProfile(profile);
			
			KeyUsageField keyUsageField = new KeyUsageField(profile, true);
			keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
			keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
			profile.addField(keyUsageField);
			
			profile = certificateProfileService.saveProfile(profile);
		}
		catch(Exception exc)
		{
			System.out.println(exc.toString());
		}
	}
	*/
	
	/**
	 * This test should only be commented out for tuning how jpa is working
	 */
	/*
	@Test
	public void jpaImplementationTest()
	{
		profile = testSupport.createProfile("jpaImplementationTest",testSupport.PROFILE_DESCRIPTION);
		
		CertificateField subject = new SubjectField(profile, "cn=$cn,o=$o");
		CertificateField issuer = new IssuerField(profile, "cn=$cn");
		CertificateField keyUsage = new KeyUsageField(profile, true);
		keyUsage.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
		keyUsage.addValue(new KeyUsageFieldValue(KeyUsageType.DATA_ENCIPHERMENT));
		keyUsage.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
		keyUsage.addValue(new KeyUsageFieldValue(KeyUsageType.KEY_ENCIPHERMENT));	
		
		profile.addField(subject);
		profile.addField(issuer);
		profile.addField(keyUsage);
		
		certificateProfileService.saveProfile(profile);
		
		CertificateProfile profile2 = new CertificateProfile();
		profile2.setProfileId(profile.getProfileId());
		profile2 = certificateProfileService.getProfile(profile2);
		profile2.getFields();
	}
	*/
	
	@After
	public void tearDown() 
	{ 
		if (profile != null)
		{
			certificateProfileService.deleteProfile(profile);
		}
	}
	

}
