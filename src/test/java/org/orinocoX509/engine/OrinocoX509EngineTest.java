package org.orinocoX509.engine;

import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.bean.CertificateInfoBean;
import org.orinocoX509.bean.CertificateValuesBean;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateProfile.KeySizeValues;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.certificate.AuthorityInformationAccessField;
import org.orinocoX509.entity.field.certificate.AuthorityKeyIdentifierField;
import org.orinocoX509.entity.field.certificate.BasicConstraintField;
import org.orinocoX509.entity.field.certificate.CRLDistributionPointField;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
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
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectAlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue.AlternativeNameType;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue.AIAType;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue.AuthorityKeyIdentifierType;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue.CertificatePolicyType;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue.ExtendedKeyUsageType;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue.KeyUsageType;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue.NetscapeCertificateTypeType;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue.QCStatementType;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue.SubjectDirectoryAttributeType;
import org.orinocoX509.service.CRLProfileService;
import org.orinocoX509.service.CertificateProfileService;
import org.orinocoX509.service.CertificateStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../applicationContext-Test.xml")
public class OrinocoX509EngineTest
{
    @Autowired
    OrinocoX509Engine engine;

    @Autowired
    TestSupport testSupport;

    @Autowired
    CertificateProfileService certificateProfileService;

    @Autowired
    CRLProfileService crlProfileService;

    @Autowired
    CertificateStatusService certificateStatusService;

    private CertificateProfile profile = null;
    private BigInteger serialNumber = null;

    @Before
    public void init()
    {
    }

    @Test
    public void generateFullCertificate()
    {
	profile = testSupport.generateBaseProfile("generateFullCertificate", TestConst.PROFILE_DESCRIPTION);

	// Authority Information Access
	BaseCertificateField aiaExtension = new AuthorityInformationAccessField(profile, false);
	aiaExtension.addValue(new AuthorityInformationAccessFieldValue(AIAType.ID_AD_CA_ISSUERS, TestConst.AIA_CA_URL));
	aiaExtension.addValue(new AuthorityInformationAccessFieldValue(AIAType.ID_AD_OCSP, TestConst.AIA_OCSP_URL));
	profile.addField(aiaExtension);

	// Authority Key Identifier
	BaseCertificateField authorityKeyIdentifier = new AuthorityKeyIdentifierField(profile, false);
	authorityKeyIdentifier.addValue(new AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType.KEY_IDENTIFIER));
	authorityKeyIdentifier.addValue(new AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType.AUTH_CERT_ISSUER_AUTH_CERT_SERIAL_NUMBER));
	profile.addField(authorityKeyIdentifier);

	// Basic constraint
	BaseCertificateField basicConstraint = new BasicConstraintField(profile, false, 0, false);
	profile.addField(basicConstraint);

	// Certificate Policy
	BaseCertificateField certificatePolicy = new CertificatePolicyField(profile, TestConst.POLICY_OID, false);
	certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.CPS, TestConst.CPS));
	certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.USER_NOTICE, TestConst.USER_NOTICE));
	profile.addField(certificatePolicy);

	// CRL Distribution Point
	BaseCertificateField crlDistributionPoint = new CRLDistributionPointField(profile, false);
	crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP1_URL1, TestConst.CRLDP1_URL2)));
	crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP2_URL1, TestConst.CRLDP2_URL2)));
	profile.addField(crlDistributionPoint);

	// Device Type
	BaseCertificateField deviceType = new DeviceTypeField(profile, TestConst.DEVICE_TYPE_TEXT, false);
	profile.addField(deviceType);

	// Extended Key Usage
	BaseCertificateField extendedKeyUsageField = new ExtendedKeyUsageField(profile, false);
	extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_CODE_SIGNING));
	extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_IPSEC_IKE));
	profile.addField(extendedKeyUsageField);

	// Issuer Alternative Name
	BaseCertificateField issuerAlternativeName = new IssuerAlternativeNameField(profile, false);
	issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.RFC822NAME, TestConst.IAN_RFC822NAME_VALUE));
	issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.DIRECTORY_NAME, TestConst.IAN_DIRECTORY_NAME_VALUE));
	profile.addField(issuerAlternativeName);

	// Key Usage
	BaseCertificateField keyUsageField = new KeyUsageField(profile, true);
	keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
	keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.DIGITAL_SIGNATURE));
	profile.addField(keyUsageField);

	// Netscape Certificate Type
	BaseCertificateField netscapeCertificateTypeField = new NetscapeCertificateTypeField(profile, false);
	netscapeCertificateTypeField.addValue(new NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType.OBJECT_SIGNING_CA));
	netscapeCertificateTypeField.addValue(new NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType.SMIME));
	profile.addField(netscapeCertificateTypeField);

	// Qualified Certificate Statements
	Integer RETENTION_PERIOD = 15;
	BaseCertificateField qcStatements = new QCStatementField(profile, false);
	qcStatements.addValue(new QCStatementFieldValue(QCStatementType.ID_ETSI_QCS_QCCOMPILANCE));
	qcStatements.addValue(new QCStatementFieldValue(QCStatementType.ID_ETSI_QCS_RETENTION_PERIOD, RETENTION_PERIOD));
	profile.addField(qcStatements);

	// Subject alternative Name
	BaseCertificateField subjectAlternativeName = new SubjectAlternativeNameField(profile, false);
	subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.RFC822NAME));
	subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.DIRECTORY_NAME, TestConst.SAN_DIRECTORY_NAME_PATTERN));
	profile.addField(subjectAlternativeName);

	// Subject Directory Attributes
	String COUNTRY_OF_CITIZENSHIP = "ES";
	String COUNTRY_OF_RESIDENCE = "UK";
	BaseCertificateField subjectDirectoryAttributes = new SubjectDirectoryAttributeField(profile, false);
	subjectDirectoryAttributes.addValue(new SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP, COUNTRY_OF_CITIZENSHIP));
	subjectDirectoryAttributes.addValue(new SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE, COUNTRY_OF_RESIDENCE));
	profile.addField(subjectDirectoryAttributes);

	// Subject Key Identifier
	BaseCertificateField subjectKeyIdentifier = new SubjectKeyIdentifierField(profile, false);
	profile.addField(subjectKeyIdentifier);

	// Values
	CertificateValuesBean values = testSupport.generateBaseValues();

	Map<String, String> subjectAltNameValues = new HashMap<String, String>(2);
	subjectAltNameValues.put("RFC822NAME", TestConst.SAN_RFC822NAME_VALUE);
	subjectAltNameValues.put("cn", "common name");
	subjectAltNameValues.put("serial_number", "12345678Z");
	values.getValues().put(FieldType.SUBJECT_ALTERNATIVE_NAME, subjectAltNameValues);

	CertificateInfoBean certificateInfo = engine.generateCertificate(profile, values);
	assertNotNull(certificateInfo.getPemCertificate());
	serialNumber = certificateInfo.getSerialNumber();

	//testSupport.saveCertificate("fullCertificate.cer", certificateInfo.getPemCertificate());

	CertificateStatus certificateStatus = engine.getCertificateStatus(certificateInfo.getSerialNumber());
	assertEquals(certificateInfo.getSerialNumber(), certificateStatus.getCertificateSerialNumber());
	assertEquals(CertificateStatusValues.V, certificateStatus.getCertificateStatus());

	CRLProfile crlProfile = testSupport.createCRLProfile("generateFullCertificate");

	assertEquals(CertificateStatusValues.V, engine.getCertificateStatus(certificateInfo.getSerialNumber()).getCertificateStatus());
	X509CRL crl = engine.generateCRL(crlProfile);
	assertNull(crl.getRevokedCertificate(certificateStatus.getCertificateSerialNumber()));

	engine.suspendCertificate(certificateInfo.getSerialNumber());
	assertEquals(CertificateStatusValues.S, engine.getCertificateStatus(certificateInfo.getSerialNumber()).getCertificateStatus());
	crl = engine.generateCRL(crlProfile);
	assertNotNull(crl.getRevokedCertificate(certificateStatus.getCertificateSerialNumber()));

	engine.restoreCertificate(certificateInfo.getSerialNumber());
	assertEquals(CertificateStatusValues.V, engine.getCertificateStatus(certificateInfo.getSerialNumber()).getCertificateStatus());
	crl = engine.generateCRL(crlProfile);
	assertNull(crl.getRevokedCertificate(certificateStatus.getCertificateSerialNumber()));

	engine.revokeCertificate(certificateInfo.getSerialNumber());
	assertEquals(CertificateStatusValues.R, engine.getCertificateStatus(certificateInfo.getSerialNumber()).getCertificateStatus());
	crl = engine.generateCRL(crlProfile);
	assertNotNull(crl.getRevokedCertificate(certificateStatus.getCertificateSerialNumber()));
    }

    // If new ca is created, remember to update the properties CA_SERIAL_NUMBER,
    // AUTHORITY_KEY_ID from test class TestConst
    // Otherwise some test will fail
    // @Test
    public void generateCACertificate()
    {
	CertificateProfile profile = new CertificateProfile("caProfile", "caProfile Description", TestConst.PROFILE_YEARS, TestConst.PROFILE_KEY_SIZE);
	BaseCertificateField subject = new SubjectField(profile, TestConst.ISSUER_PATTERN);
	BaseCertificateField issuer = new IssuerField(profile, TestConst.ISSUER_PATTERN);
	profile.addField(subject);
	profile.addField(issuer);

	// Basic constraint
	BaseCertificateField basicConstraint = new BasicConstraintField(profile, true, 0, false);
	profile.addField(basicConstraint);

	// Certificate Policy
	BaseCertificateField certificatePolicy = new CertificatePolicyField(profile, TestConst.POLICY_OID, false);
	certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.CPS, TestConst.CPS));
	certificatePolicy.addValue(new CertificatePolicyFieldValue(CertificatePolicyType.USER_NOTICE, TestConst.USER_NOTICE));
	profile.addField(certificatePolicy);

	// CRL Distribution Point
	BaseCertificateField crlDistributionPoint = new CRLDistributionPointField(profile, false);
	crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP1_URL1, TestConst.CRLDP1_URL2)));
	crlDistributionPoint.addValue(new CRLDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP2_URL1, TestConst.CRLDP2_URL2)));
	profile.addField(crlDistributionPoint);

	// Issuer Alternative Name
	BaseCertificateField issuerAlternativeName = new IssuerAlternativeNameField(profile, false);
	issuerAlternativeName.addValue(new IssuerAlternativeNameFieldValue(AlternativeNameType.RFC822NAME, TestConst.IAN_RFC822NAME_VALUE));
	profile.addField(issuerAlternativeName);

	// Key Usage
	BaseCertificateField keyUsageField = new KeyUsageField(profile, true);
	keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.KEY_CERT_SIGN));
	keyUsageField.addValue(new KeyUsageFieldValue(KeyUsageType.CRL_SIGN));
	profile.addField(keyUsageField);

	// Subject alternative Name
	BaseCertificateField subjectAlternativeName = new SubjectAlternativeNameField(profile, false);
	subjectAlternativeName.addValue(new SubjectAlternativeNameFieldValue(AlternativeNameType.RFC822NAME));
	profile.addField(subjectAlternativeName);

	// Subject Key Identifier
	BaseCertificateField subjectKeyIdentifier = new SubjectKeyIdentifierField(profile, false);
	profile.addField(subjectKeyIdentifier);

	// Values
	CertificateValuesBean values = new CertificateValuesBean();

	Map<String, String> subjectAltNameValues = new HashMap<String, String>(2);
	subjectAltNameValues.put("RFC822NAME", "mail@cadomain.com");
	values.getValues().put(FieldType.SUBJECT_ALTERNATIVE_NAME, subjectAltNameValues);

	// Generate certificate
	CertificateInfoBean certificateInfo = engine.generateCACertificate(profile, values, KeySizeValues.SIZE_1024);
	assertNotNull(certificateInfo.getPemCertificate());

    }

    @After
    public void tearDown()
    {
	if (profile != null)
	{
	    certificateProfileService.deleteProfile(profile);
	}

	certificateStatusService.deleteStatus(certificateStatusService.getStatus(new CertificateStatus(serialNumber)));
    }
}
