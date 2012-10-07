package org.orinocoX509.service;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.PolicyQualifierId;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.UserNotice;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.field.certificate.AuthorityInformationAccessField;
import org.orinocoX509.entity.field.certificate.AuthorityKeyIdentifierField;
import org.orinocoX509.entity.field.certificate.BasicConstraintField;
import org.orinocoX509.entity.field.certificate.CRLDistributionPointField;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.entity.field.certificate.CertificatePolicyField;
import org.orinocoX509.entity.field.certificate.DeviceTypeField;
import org.orinocoX509.entity.field.certificate.ExtendedKeyUsageField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.entity.field.certificate.IssuerAlternativeNameField;
import org.orinocoX509.entity.field.certificate.KeyUsageField;
import org.orinocoX509.entity.field.certificate.NetscapeCertificateTypeField;
import org.orinocoX509.entity.field.certificate.QCStatementField;
import org.orinocoX509.entity.field.certificate.SubjectAlternativeNameField;
import org.orinocoX509.entity.field.certificate.SubjectDirectoryAttributeField;
import org.orinocoX509.entity.field.certificate.SubjectKeyIdentifierField;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue;
import org.orinocoX509.entity.value.certificate.CRLDistributionPointFieldValue;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.IssuerAlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue.QCStatementType;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue.SubjectDirectoryAttributeType;
import org.orinocoX509.entity.value.certificate.SubjectAlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue.AlternativeNameType;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue.AIAType;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue.AuthorityKeyIdentifierType;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue.CertificatePolicyType;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue.ExtendedKeyUsageType;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue.KeyUsageType;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue.NetscapeCertificateTypeType;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.holder.CertificateInfo;
import org.orinocoX509.holder.CertificateValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CertificateServiceTest 
{
	@Autowired
	private CertificateService certificateService;
	
	private CertificateProfile profile = null;
	
	@Autowired
	TestSupport testSupport;
	
	private enum WrongProfileTest 
	{
		MISSING_SUBJECT,
		MISSING_ISSUER,
		WRONG_YEARS
	}
	
	
	@Before
	public void init() { }
	
	
	private CertificateProfile generateWrongProfileTest(WrongProfileTest wrongProfileTest,String profileName)
	{
		CertificateProfile profile = testSupport.generateBaseProfile(profileName,TestConst.PROFILE_DESCRIPTION);
		
		switch(wrongProfileTest)
		{
			case MISSING_SUBJECT:
				profile.getFields().remove(profile.getField(FieldType.SUBJECT));
			break;
			case MISSING_ISSUER:
				profile.getFields().remove(profile.getField(FieldType.SUBJECT));
			break;
			case WRONG_YEARS:
				profile.setYears(0);
			break;
		}
		
		return(profile);
	}
	
	private void generateCertificateException(CertificateProfile profile, CertificateValues certificateValues, EngineErrorCodes engineErrorCodeExpected)
	{
		try
		{
			certificateService.generateCertificate(profile, certificateValues);
			fail();
		}
		catch(EngineException exc)
		{
			assertEquals(engineErrorCodeExpected ,exc.getErrorCode());
	
		}
	}
	
	@Test
	public void generateCertificateWithoutSubjectTest()
	{
		profile = generateWrongProfileTest(WrongProfileTest.MISSING_SUBJECT,"generateCertificateWithoutSubjectTest");
		generateCertificateException(profile, null, EngineErrorCodes.PROFILE_MALFORMED);
	}
	
	@Test
	public void generateCertificateWithoutIssuerTest()
	{
		profile = generateWrongProfileTest(WrongProfileTest.MISSING_ISSUER, "generateCertificateWithoutIssuerTest");
		generateCertificateException(profile, null, EngineErrorCodes.PROFILE_MALFORMED);
	}
	
	@Test
	public void generateCertificateWrongYearsTest()
	{
		profile = generateWrongProfileTest(WrongProfileTest.WRONG_YEARS, "generateCertificateWrongYearsTest");
		generateCertificateException(profile, null, EngineErrorCodes.PROFILE_MALFORMED);
	}
	
	@Test
	public void generateCertificateEmptyRequestTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateEmptyRequestTest",TestConst.PROFILE_DESCRIPTION);
		generateCertificateException(profile, null, EngineErrorCodes.WRONG_CERTIFICATE_VALUES);
	}
	
	@Test
	public void generateCertificateWrongRequestTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateWrongRequestTest",TestConst.PROFILE_DESCRIPTION);
		CertificateValues values = new CertificateValues();
		values.setRequest("1234567890");
		generateCertificateException(profile, values, EngineErrorCodes.WRONG_PKCS10_FORMAT);
	}
	
	// Authority Information Access
	
	@Test
	public void generateCertificateAIAExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateAIAExtensionTest", TestConst.PROFILE_DESCRIPTION);
		
		CertificateField aiaExtension = new AuthorityInformationAccessField(profile, false);
		aiaExtension.addValue(new AuthorityInformationAccessFieldValue(AIAType.ID_AD_CA_ISSUERS, TestConst.AIA_CA_URL));
		aiaExtension.addValue(new AuthorityInformationAccessFieldValue(AIAType.ID_AD_OCSP, TestConst.AIA_OCSP_URL));
		profile.addField(aiaExtension);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);

		//testSupport.saveCertificate("aiaExtension.cer", certificateInfo.getPemCertificate());
		
		// test values
		try
		{
			byte[] aiaExtensionDER = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("1.3.6.1.5.5.7.1.1");
			assertNotNull(aiaExtensionDER);
			
			DEROctetString derOStr = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(aiaExtensionDER)).readObject());
			
			ASN1InputStream asn1is = new ASN1InputStream(derOStr.getOctets());
			ASN1Sequence seq =  (ASN1Sequence) asn1is.readObject();
			
			AccessDescription ad = AccessDescription.getInstance((ASN1Sequence) seq.getObjectAt(0).toASN1Primitive());
			//System.out.println("Access Method: " + ad.getAccessMethod());
			GeneralName gn = ad.getAccessLocation();
			assertEquals(TestConst.AIA_CA_URL,gn.getName().toString());
	
			ad = AccessDescription.getInstance((ASN1Sequence) seq.getObjectAt(1).toASN1Primitive());
			gn = ad.getAccessLocation();
			//System.out.println("Access Method: " + ad.getAccessMethod());
			assertEquals(TestConst.AIA_OCSP_URL,gn.getName().toString());
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}

	// Authority Key Identifier
	
	@Test
	public void generateCertificateAKIExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateAKIExtensionTest", TestConst.PROFILE_DESCRIPTION);
		
		CertificateField authorityKeyIdentifier = new AuthorityKeyIdentifierField(profile, false);
		authorityKeyIdentifier.addValue(new AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType.KEY_IDENTIFIER));
		authorityKeyIdentifier.addValue(new AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType.AUTH_CERT_ISSUER_AUTH_CERT_SERIAL_NUMBER));
		profile.addField(authorityKeyIdentifier);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("akiExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			byte[] aki = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("2.5.29.35");
			assertNotNull(aki);
		
			DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(aki)).readObject());
			AuthorityKeyIdentifier keyId = AuthorityKeyIdentifier.getInstance((ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject());

			assertEquals(TestConst.ROOT_SUBJECT,  (keyId.getAuthorityCertIssuer().getNames()[0]).getName().toString());
			assertArrayEquals(TestConst.AUTHORITY_KEY_ID, keyId.getKeyIdentifier());
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Basic constraint
	
	@Test
	public void generateCertificateBasicConstrainTest()
	{
		Boolean CRITICAL = false;
		Boolean ISCA = true;
		Integer PATH_LENGTH = 1;
		
		profile = testSupport.generateBaseProfile("generateCertificateBasicConstrainTest", TestConst.PROFILE_DESCRIPTION);
		
		CertificateField basicConstraint = new BasicConstraintField(profile, ISCA, PATH_LENGTH, CRITICAL);

		profile.addField(basicConstraint);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		testSupport.saveCertificate("bcExtension.cer", certificateInfo.getPemCertificate());
		
		try
		{
			Integer pathLength = testSupport.getCertificate(certificateInfo.getPemCertificate()).getBasicConstraints();
			// If isCA is false pathLength is -1 because it is not a CA certificate!
			assertEquals(PATH_LENGTH, pathLength);
		}
		catch(Exception exc)
		{
			System.out.println(exc.toString());
			fail();
		}
	}	
	
	// Certificate Policies
	
	@Test
	public void generateCertificateCertificatePoliciesExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateCertificatePoliciesExtensionTest", TestConst.PROFILE_DESCRIPTION);
		
		CertificateField certificatePolicy = new CertificatePolicyField(profile, TestConst.POLICY_OID, false);
		certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.CPS, TestConst.CPS ));
		certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.USER_NOTICE, TestConst.USER_NOTICE ));
		profile.addField(certificatePolicy);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("cpExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			byte[] cpExtension = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("2.5.29.32");
			assertNotNull(cpExtension);
			
			DEROctetString derOStr = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(cpExtension)).readObject());
			ASN1InputStream asn1is = new ASN1InputStream(derOStr.getOctets());
			ASN1Sequence seq =  (ASN1Sequence) asn1is.readObject();
			
			for(int i=0; i < seq.size(); i++)
			{
				PolicyInformation policyInf = PolicyInformation.getInstance((ASN1Sequence) seq.getObjectAt(i).toASN1Primitive());
				assertEquals(TestConst.POLICY_OID, policyInf.getPolicyIdentifier().getId());
				
				for(int u=0; u < policyInf.getPolicyQualifiers().size(); u++)
				{
					PolicyQualifierInfo pqi = new PolicyQualifierInfo((ASN1Sequence) policyInf.getPolicyQualifiers().getObjectAt(u));
					if(PolicyQualifierId.id_qt_cps.equals(pqi.getPolicyQualifierId()) == true)
					{
						assertEquals(TestConst.CPS, pqi.getQualifier().toString());
					}
					else if (PolicyQualifierId.id_qt_unotice.equals(pqi.getPolicyQualifierId()) == true)
					{
						UserNotice usrNotice = UserNotice.getInstance((ASN1Sequence) pqi.getQualifier());
						assertEquals(TestConst.USER_NOTICE, usrNotice.getExplicitText().getString());
					}
				}
			}
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// CRL Distribution Point
	
	@Test
	public void generateCertificateCRLDistributionPointExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateCRLDistributionPointExtensionTest", TestConst.PROFILE_DESCRIPTION);
		
		CertificateField crlDistributionPoint = new CRLDistributionPointField(profile, false);
		crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP1_URL1, TestConst.CRLDP1_URL2)));
		crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP2_URL1, TestConst.CRLDP2_URL2)));
		profile.addField(crlDistributionPoint);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("crlDpExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			byte[] crldpExtension = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("2.5.29.31");
			assertNotNull(crldpExtension);
			
			DEROctetString derOStr = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(crldpExtension)).readObject());
			
			ASN1InputStream asn1is = new ASN1InputStream(derOStr.getOctets());
			ASN1Sequence seq =  (ASN1Sequence) asn1is.readObject();
			assertEquals(2, seq.size());
			
			for(int i=0; i < seq.size(); i++)
			{
				DistributionPoint crldp = new DistributionPoint((ASN1Sequence) seq.getObjectAt(i).toASN1Primitive());
				GeneralNames gns = (GeneralNames) crldp.getDistributionPoint().getName();
				
				if (i==0)
				{
					if ((gns.getNames()[0].getName().toString().equalsIgnoreCase(TestConst.CRLDP1_URL1) == false) ||
							(gns.getNames()[1].getName().toString().equalsIgnoreCase(TestConst.CRLDP1_URL2) == false))
					{
						fail();
					}
				}
				else
				{
					if ((gns.getNames()[0].getName().toString().equalsIgnoreCase(TestConst.CRLDP2_URL1) == false) ||
							(gns.getNames()[1].getName().toString().equalsIgnoreCase(TestConst.CRLDP2_URL2) == false))
					{
						fail();
					}
				}
				gns.getNames()[1].getName().toString();
			}
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Device Type
	
	@Test
	public void generateCertificateDeviceTypeExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateDeviceTypeExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField deviceType = new DeviceTypeField(profile, TestConst.DEVICE_TYPE_TEXT, false);
		profile.addField(deviceType);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("dtExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			byte[] dtExt = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("1.3.6.1.4.1.15096.1.4.2");
			DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(dtExt)).readObject());
			oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject());
			assertEquals(TestConst.DEVICE_TYPE_TEXT, new String(oct.getOctets()));
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
		
	// Extended Key Usage
	
	@Test
	public void generateCertificateExtendedKeyUsageExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateExtendedKeyUsageExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField extendedKeyUsageField = new ExtendedKeyUsageField(profile, false);
		extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_CODE_SIGNING));	
		extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_IPSEC_IKE));
		profile.addField(extendedKeyUsageField);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("ekuExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			List<String> keyUsageList = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtendedKeyUsage();
			assertEquals(KeyPurposeId.id_kp_codeSigning.toString(), keyUsageList.get(0));
			assertEquals(KeyPurposeId.id_kp_ipsecIKE.toString(), keyUsageList.get(1));
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Issuer Alternative Name
	
	@Test
	public void generateCertificateIssuerAlternativeNameExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateIssuerAlternativeNameExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField issuerAlternativeName = new IssuerAlternativeNameField(profile, false);
		issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.RFC822NAME, TestConst.IAN_RFC822NAME_VALUE));
		issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.DIRECTORY_NAME, TestConst.IAN_DIRECTORY_NAME_VALUE));
		profile.addField(issuerAlternativeName);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("aneExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			Iterator<List<?>> issuerAltNameIterator = testSupport.getCertificate(certificateInfo.getPemCertificate()).getIssuerAlternativeNames().iterator();
			while(issuerAltNameIterator.hasNext() == true)
			{
				String value = (String)issuerAltNameIterator.next().get(1); 
				if ((value.startsWith(TestConst.IAN_DIRECTORY_NAME_VALUE) == false) && (value.startsWith(TestConst.IAN_RFC822NAME_VALUE) == false)) 
				{
					fail();
				}
			}
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Key Usage
	
	@Test
	public void generateCertificateKeyUsageExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateKeyUsageExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField keyUsageField = new KeyUsageField(profile, true);
		keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
		keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
		profile.addField(keyUsageField);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("kuExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			boolean keyUsage[] = testSupport.getCertificate(certificateInfo.getPemCertificate()).getKeyUsage();
			if (testSupport.compareArray(TestConst.KEY_USAGES, keyUsage) == false)
			{
				fail();
			}
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Qualified Certificate Statements
	
	@Test
	public void generateCertificateQualifiedCertificateStatementsTest()
	{
		int RETENTION_PERIOD = 15; 
		profile = testSupport.generateBaseProfile("generateQualifiedCertificateStatementsExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField qcStatements = new QCStatementField(profile, false);
		qcStatements.addValue(new QCStatementFieldValue(QCStatementType.ID_ETSI_QCS_QCCOMPILANCE));
		qcStatements.addValue(new QCStatementFieldValue(QCStatementType.ID_ETSI_QCS_RETENTION_PERIOD,RETENTION_PERIOD));
		profile.addField(qcStatements);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("qcStatements.cer", certificateInfo.getPemCertificate());
		
		try
		{
			byte[] qcStatementsExtension = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("1.3.6.1.5.5.7.1.3");
			assertNotNull(qcStatementsExtension);
			
			DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(qcStatementsExtension)).readObject());
			DLSequence qcsSeq = (DLSequence) (new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject());
			
			for(int i=0; i < qcsSeq.size(); i++)
			{
				DLSequence qcsItemSeq = (DLSequence) qcsSeq.getObjectAt(i);
				QCStatement qcs = new QCStatement((ASN1ObjectIdentifier) qcsItemSeq.getObjectAt(0));
				if ((qcs.getStatementId().toString().equals(QCStatement.id_etsi_qcs_QcCompliance.toString())  == false) &&
						(qcs.getStatementId().toString().equals(QCStatement.id_etsi_qcs_RetentionPeriod.toString())  == false))
				{
					fail();
				}
				else
				{
					if (qcs.getStatementId().toString().equals(QCStatement.id_etsi_qcs_RetentionPeriod.toString())  == true)
					{
						ASN1Integer retentionPeriod = (ASN1Integer) qcsItemSeq.getObjectAt(1);
						assertEquals(RETENTION_PERIOD, retentionPeriod.getValue().intValue());
					}
				}
			}
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Netscape Certificate Type
	
	@Test
	public void generateCertificateNetscapeCertificateTypeExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateNetscapeCertificateTypeExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField netscapeCertificateTypeField = new NetscapeCertificateTypeField(profile, false);
		netscapeCertificateTypeField.addValue(new NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType.OBJECT_SIGNING_CA));
		netscapeCertificateTypeField.addValue(new NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType.SMIME));
		profile.addField(netscapeCertificateTypeField);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("nctExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			byte[] netscapeCertificateTypeExtension = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("2.16.840.1.113730.1.1");
			assertNotNull(netscapeCertificateTypeExtension);
			
			DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(netscapeCertificateTypeExtension)).readObject());
			DERBitString bstr = (DERBitString) (new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject());
			
			NetscapeCertType t = new NetscapeCertType(bstr);
			assertEquals(TestConst.NETSCAPE_CERTIFICATE_TYPES,t.intValue());
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Subject Alternative Name
	
	@Test
	public void generateCertificateSubjectAlternativeNameExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateSubjectAlternativeNameExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField subjectAlternativeName = new SubjectAlternativeNameField(profile, false);
		subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.RFC822NAME));
		subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.DIRECTORY_NAME, TestConst.SAN_DIRECTORY_NAME_PATTERN));
		profile.addField(subjectAlternativeName);
		
		CertificateValues values = testSupport.generateBaseValues();
		
		Map<String, String> subjectAltNameValues = new HashMap<String, String>(2);
		subjectAltNameValues.put("RFC822NAME", TestConst.SAN_RFC822NAME_VALUE);
		subjectAltNameValues.put("cn", "common name");
		subjectAltNameValues.put("serial_number", "12345678Z");
		values.getValues().put(FieldType.SUBJECT_ALTERNATIVE_NAME, subjectAltNameValues);
		
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("sanExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			Iterator<List<?>> subjectAltNameIterator = testSupport.getCertificate(certificateInfo.getPemCertificate()).getSubjectAlternativeNames().iterator();
			while(subjectAltNameIterator.hasNext() == true)
			{
				String value = (String)subjectAltNameIterator.next().get(1); 
				if (value.equalsIgnoreCase(TestConst.SAN_RFC822NAME_VALUE) == true)
				{
					continue;
				}
				
				if (value.equalsIgnoreCase(TestConst.SAN_DIRECTORY_NAME_VALUE) == true)
				{
					continue;
				}
				
				fail();
			}
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	// Subject Directory Attributes
	
	@Test
	public void generateCertificateSubjectDirectoryAttributesExtensionTest()
	{
		String COUNTRY_OF_CITIZENSHIP = "ES";
		String COUNTRY_OF_RESIDENCE = "UK";
		
		profile = testSupport.generateBaseProfile("generateCertificateSubjectDirectoryAttributesExtensionTest", TestConst.PROFILE_DESCRIPTION);
		
		CertificateField subjectDirectoryAttributes = new SubjectDirectoryAttributeField(profile, false);
		subjectDirectoryAttributes.addValue(new SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP, COUNTRY_OF_CITIZENSHIP));
		subjectDirectoryAttributes.addValue(new SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE, COUNTRY_OF_RESIDENCE));
		profile.addField(subjectDirectoryAttributes);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		testSupport.saveCertificate("sdaExtention.cer", certificateInfo.getPemCertificate());
		
		try
		{
			byte[] sdaExtension = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("2.5.29.9");
			assertNotNull(sdaExtension);
			
			DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(sdaExtension)).readObject());
			DLSequence sdaSeq = (DLSequence) (new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject());
			
			for(int i=0; i < sdaSeq.size(); i++)
			{
				DLSequence sdaItemSeq = (DLSequence) sdaSeq.getObjectAt(i);
				
				ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) sdaItemSeq.getObjectAt(0);
				DLSet valueSet = (DLSet) sdaItemSeq.getObjectAt(1);
				
				if (oid.toString().compareTo(SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP.toString()) == 0)
				{
					assertEquals(COUNTRY_OF_CITIZENSHIP, valueSet.getObjectAt(0).toString());
				}
				else if (oid.toString().compareTo(SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE.toString()) == 0)
				{
					assertEquals(COUNTRY_OF_RESIDENCE, valueSet.getObjectAt(0).toString());
				}
				else
				{
					fail();
				}
			}
		}
		catch(Exception exc)
		{
			System.out.println(exc.toString());
			fail();
		}
	}
	
	// Subject Key Identifier
	
	@Test
	public void generateCertificateSubjectKeyIdentifierExtensionTest()
	{
		profile = testSupport.generateBaseProfile("generateCertificateSubjectKeyIdentifierExtensionTest", TestConst.PROFILE_DESCRIPTION);
		CertificateField subjectKeyIdentifier = new SubjectKeyIdentifierField(profile, false);
		profile.addField(subjectKeyIdentifier);
		
		CertificateValues values = testSupport.generateBaseValues();
		CertificateInfo certificateInfo = certificateService.generateCertificate(profile, values);
		
		//testSupport.saveCertificate("skiExtension.cer", certificateInfo.getPemCertificate());
		
		// test value
		try
		{
			byte[] ski = testSupport.getCertificate(certificateInfo.getPemCertificate()).getExtensionValue("2.5.29.14");
			assertNotNull(ski);
			
			DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(ski)).readObject());
			oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject());
			
			assertArrayEquals(TestConst.SUBJECT_KEY_IDENTIFIER, oct.getOctets());
		}
		catch(Exception exc)
		{
			//System.out.println(exc.toString());
			fail();
		}
	}
	
	
	/*
	@Test
	public void generateCertificateTest()
	{
		profile = testSupport.createEmptyProfile("generateCertificateTest",testSupport.PROFILE_DESCRIPTION);
		
		SubjectField subject = new SubjectField(profile, "o=$o,cn=$cn");
		IssuerField issuer = new IssuerField(profile, "o=Organization,cn=ISSUER");
		
		KeyUsageField keyUsage = new KeyUsageField(profile, true);
		//keyUsage.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
		keyUsage.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
		
		profile.addField(subject);
		profile.addField(issuer);
		profile.addField(keyUsage);
		
		Map<String, String> subjectValues = new HashMap<String, String>(2);
		subjectValues.put("cn", "common name");
		subjectValues.put("o", "my organization");
		
		CertificateValues certificateValues = new CertificateValues();
		certificateValues.getValues().put(FieldType.SUBJECT, subjectValues);
		certificateValues.setRequest(testSupport.PKCS10RequestTest);
		
		CertificateInfo certificateInfo = engine.generateCertificate(profile, certificateValues);
		
		testSupport.saveCertificate("test.cer",certificateInfo.getPemCertificate());
	}
	*/
	
	@After
	public void tearDown()  {  }

}
