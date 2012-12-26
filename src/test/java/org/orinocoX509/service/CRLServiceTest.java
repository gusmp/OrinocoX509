package org.orinocoX509.service;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.X509Extension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.crl.AuthorityKeyIdentifierCRLField;
import org.orinocoX509.entity.field.crl.IssuingDistributionPointField;
import org.orinocoX509.entity.field.crl.CRLNumberField;
import org.orinocoX509.entity.field.crl.TimeNextUpdateField;
import org.orinocoX509.entity.value.crl.IssuingDistributionPointFieldValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../applicationContext-Test.xml")
public class CRLServiceTest
{
    @Autowired
    CRLService crlService;

    @Autowired
    CertificateStatusService certificateStatusService;

    @Autowired
    CRLProfileService crlProfileService;

    @Autowired
    TestSupport testSupport;

    private CRLProfile crlProfile = null;

    @Before
    public void init()
    {
	// clean CRL cache
	testSupport.cleanCRLCache(certificateStatusService);
    }

    /*
     * private void saveCRL(String fileName, X509CRL crl)
     * {
     * try
     * {
     * testSupport.saveFile(fileName, crl.getEncoded());
     * }
     * catch(CRLException exc) {}
     * }
     */

    @Test
    public void generateEmptyCRL()
    {
	crlProfile = testSupport.createCRLProfile("generateEmptyCRL");
	X509CRL crl = crlService.generateCRL(crlProfile);
	assertNotNull(crl);
	// saveCRL("crl_empty.crl", crl);
    }

    // Time Next Update

    @Test
    public void generateTimeNextUpdateCRL()
    {
	crlProfile = testSupport.createCRLProfile("generateTimeNextUpdateCRL");
	crlProfile.addField(new TimeNextUpdateField(crlProfile, 7));
	X509CRL crl = crlService.generateCRL(crlProfile);
	assertNotNull(crl);
	// saveCRL("crl_time_next_update.crl", crl);
	assertNotNull(crl.getNextUpdate());
    }

    // Authority Key Identifier

    public void generateAuthorityKeyIdentifierCRL(String profileName, Boolean critical, Boolean authorityKeyIdentifier, Boolean authorityIssuerSerialNumber, String fileName)
    {
	crlProfile = testSupport.createCRLProfile(profileName);
	crlProfile.addField(new AuthorityKeyIdentifierCRLField(crlProfile, critical, authorityKeyIdentifier, authorityIssuerSerialNumber));
	X509CRL crl = crlService.generateCRL(crlProfile);
	assertNotNull(crl);
	// saveCRL(fileName, crl);

	try
	{
	    byte[] akiASN1 = crl.getExtensionValue(X509Extension.authorityKeyIdentifier.toString());
	    DEROctetString derOStr = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(akiASN1)).readObject());
	    AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(derOStr.getOctets());
	    assertNotNull(akiASN1);

	    if (authorityKeyIdentifier == true)
	    {
		assertArrayEquals(TestConst.AUTHORITY_KEY_ID, aki.getKeyIdentifier());
	    }

	    if (authorityIssuerSerialNumber == true)
	    {
		assertEquals(TestConst.CA_SERIAL_NUMBER, aki.getAuthorityCertSerialNumber());
	    }
	}
	catch (Exception exc)
	{
	    fail();
	}

    }

    @Test
    public void generateFullAuthorityKeyIdentifierCRL()
    {
	generateAuthorityKeyIdentifierCRL("generateFullAuthorityKeyIdentifierCRL", false, true, true, "crl_aki.crl");
    }

    @Test
    public void generateAuthorityKeyIdentifierCRL_AKI_Only()
    {
	generateAuthorityKeyIdentifierCRL("generateAuthorityKeyIdentifierCRL_AKI_Only", false, true, false, "crl_aki_aki_only.crl");
    }

    @Test
    public void generateAuthorityKeyIdentifierCRL_ISNOnly()
    {
	generateAuthorityKeyIdentifierCRL("generateAuthorityKeyIdentifierCRL_AKI_Only", false, false, true, "crl_aki_isn_only.crl");
    }

    // Issuing Distribution Point

    @Test
    public void generateIssuingDistributionPointCRL()
    {
	Boolean CRITICAL = false;

	crlProfile = testSupport.createCRLProfile("generateIssuingDistributionPointCRL");
	IssuingDistributionPointField issuingDistributionPoint = new IssuingDistributionPointField(crlProfile, CRITICAL);
	issuingDistributionPoint.addValue(new IssuingDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP1_URL1, TestConst.CRLDP1_URL2), false, false));
	crlProfile.addField(issuingDistributionPoint);

	X509CRL crl = crlService.generateCRL(crlProfile);
	assertNotNull(crl);
	// saveCRL("crl_issuing_distribution_point.crl", crl);

	// testear los valores devueltos
	try
	{
	    byte[] idp = crl.getExtensionValue("2.5.29.28");
	    DEROctetString derOStr = (DEROctetString) new ASN1InputStream(new ByteArrayInputStream(idp)).readObject();
	    ASN1InputStream asn1is = new ASN1InputStream(derOStr.getOctets());

	    ASN1Sequence seq = (ASN1Sequence) asn1is.readObject();

	    IssuingDistributionPoint issuingDistributionPt = IssuingDistributionPoint.getInstance(seq);
	    GeneralNames generalNames = (GeneralNames) issuingDistributionPt.getDistributionPoint().getName();
	    assertEquals(TestConst.CRLDP1_URL1, generalNames.getNames()[0].getName().toString());
	    assertEquals(TestConst.CRLDP1_URL2, generalNames.getNames()[1].getName().toString());
	    
	    asn1is.close();
	}
	catch (Exception exc)
	{
	    System.out.println("Exc: " + exc.toString());
	    fail();
	}
    }

    // CRL Number

    @Test
    public void generateCRLNumberCRL()
    {
	crlProfile = testSupport.createCRLProfile("generateCRLNumberCRL");
	crlProfile.addField(new CRLNumberField(crlProfile, false));
	X509CRL crl = crlService.generateCRL(crlProfile);
	assertNotNull(crl);
	// saveCRL("crl_crl_number.crl", crl);

	try
	{
	    byte[] crlNumberASN1 = crl.getExtensionValue(X509Extension.cRLNumber.toString());
	    assertNotNull(crlNumberASN1);

	    // DEROctetString derOStr = (DEROctetString) (new
	    // ASN1InputStream(new
	    // ByteArrayInputStream(crlNumberASN1)).readObject());
	    // ASN1InputStream asn1is = new
	    // ASN1InputStream(derOStr.getOctets());
	    // System.out.println("CRL Number: " + new
	    // BigInteger(asn1is.readObject().toString()));
	}
	catch (Exception exc)
	{
	    fail();
	}
    }

    @Test
    public void generateCRLWithEntries()
    {
	crlProfile = testSupport.createCRLProfile("generateCRLWithEntries");

	certificateStatusService.saveStatus(testSupport.generateCertificateStatus(TestConst.SERIAL_NUMBER_1, CertificateStatusValues.R, null, null));
	certificateStatusService.saveStatus(testSupport.generateCertificateStatus(TestConst.SERIAL_NUMBER_2, CertificateStatusValues.S, null, null));

	X509CRL crl = crlService.generateCRL(crlProfile);

	certificateStatusService.deleteStatus(certificateStatusService.getStatus(new CertificateStatus(TestConst.SERIAL_NUMBER_1)));
	certificateStatusService.deleteStatus(certificateStatusService.getStatus(new CertificateStatus(TestConst.SERIAL_NUMBER_2)));

	assertNotNull(crl);
	// saveCRL("crl_entries.crl", crl);

	assertNotNull(crl.getRevokedCertificate(TestConst.SERIAL_NUMBER_1));
	assertNotNull(crl.getRevokedCertificate(TestConst.SERIAL_NUMBER_2));
    }

    @Test
    public void generateCRLStepNumberCRL()
    {
	crlProfile = testSupport.createCRLProfile("generateCRLStepNumberCRL");
	crlProfile.addField(new CRLNumberField(crlProfile, false));
	crlProfileService.saveProfile(crlProfile);

	X509CRL crl1 = crlService.generateCRL(crlProfile);
	// saveCRL("crl_step1.crl", crl1);
	testSupport.cleanCRLCache(certificateStatusService);

	try
	{
	    byte[] crlNumberASN1 = crl1.getExtensionValue(X509Extension.cRLNumber.toString());
	    DEROctetString derOStr = (DEROctetString) new ASN1InputStream(new ByteArrayInputStream(crlNumberASN1)).readObject();
	    ASN1InputStream asn1is = new ASN1InputStream(derOStr.getOctets());
	    BigInteger crlNumber1 = new BigInteger(asn1is.readObject().toString());
	    // System.out.println("CRL Number 1: " + crlNumber1);

	    X509CRL crl2 = crlService.generateCRL(crlProfile);
	    // saveCRL("crl_step2.crl", crl2);
	    crlNumberASN1 = crl2.getExtensionValue(X509Extension.cRLNumber.toString());
	    derOStr = (DEROctetString) new ASN1InputStream(new ByteArrayInputStream(crlNumberASN1)).readObject();
	    asn1is.close();
	    asn1is = new ASN1InputStream(derOStr.getOctets());

	    BigInteger crlNumber2 = new BigInteger(asn1is.readObject().toString());
	    // System.out.println("CRL Number 2: " + crlNumber2);

	    asn1is.close();
	    assertEquals(crlNumber1.intValue() + 1, crlNumber2.intValue());
	}
	catch (Exception exc)
	{
	    System.out.println("Exc: " + exc.toString());
	    fail();
	}
    }

    private List<String> getCrlDpList()
    {
	List<String> crldpList = new ArrayList<String>(2);
	crldpList.add(TestConst.CRL_EC_IDCAT_1);
	crldpList.add(TestConst.CRL_EC_IDCAT_2);
	return (crldpList);
    }

    private X509Certificate getCertificate()
    {
	try
	{
	    ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(TestConst.VALID_EC_IDCAT_CERTIFICATE);
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    X509Certificate certificate = (X509Certificate) cf.generateCertificate(bArrayInputStream);
	    bArrayInputStream.close();
	    return (certificate);
	}
	catch (Exception exc)
	{
	    fail();
	    return (null);
	}
    }

    // These tests require Internet connection

    @Test
    public void getStatusSerialNumberCRLDPList()
    {
	CertificateStatusValues certificateStatusValue = crlService.getStatus(TestConst.SERIAL_NUMBER_VALID, getCrlDpList());
	assertEquals(CertificateStatusValues.V, certificateStatusValue);
    }

    @Test
    public void getStatusX509Certificate()
    {
	CertificateStatusValues certificateStatusValue = crlService.getStatus(getCertificate());
	assertEquals(CertificateStatusValues.V, certificateStatusValue);
    }

    @Test
    public void getStatusX509CertificateCRLDPList()
    {
	CertificateStatusValues certificateStatusValue = crlService.getStatus(getCertificate(), getCrlDpList());
	assertEquals(CertificateStatusValues.V, certificateStatusValue);
    }

    @After
    public void tearDown()
    {
	if (crlProfile != null)
	{
	    crlProfileService.deleteProfile(crlProfile);
	}
    }
}
