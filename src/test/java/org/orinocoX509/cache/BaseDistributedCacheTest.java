package org.orinocoX509.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.security.cert.X509CRL;

import org.junit.After;
import org.junit.Test;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.field.certificate.ExtendedKeyUsageField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.entity.field.certificate.IssuerField;
import org.orinocoX509.entity.field.certificate.SubjectField;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue.ExtendedKeyUsageType;
import org.orinocoX509.service.CRLProfileService;
import org.orinocoX509.service.CRLService;
import org.orinocoX509.service.CertificateProfileService;
import org.orinocoX509.service.CertificateStatusService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDistributedCacheTest
{
    @Autowired
    protected CRLProfileService crlProfileService;

    @Autowired
    protected CertificateStatusService certificateStatusService;

    @Autowired
    protected TestSupport testSupport;

    @Autowired
    protected CRLService crlService;

    @Autowired
    protected CertificateProfileService certificateProfileService;

    protected CertificateProfile certificateProfile = null;
    protected CRLProfile crlProfile = null;
    protected CertificateStatus certificateStatus = null;

    @Test
    public void testCRLCache()
    {
	crlProfile = testSupport.createCRLProfile("testCRLDistributedCache");

	certificateStatus = testSupport.generateCertificateStatus(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER, CertificateStatusValues.S, null, null);
	certificateStatus = certificateStatusService.saveStatus(certificateStatus);

	try
	{
	    // Generates a fresh CRL with DUMMY cert
	    X509CRL crl1 = crlService.generateCRL(crlProfile);
	    assertNotNull(crl1.getRevokedCertificate(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER));
	    Thread.sleep(2000);

	    // Hit the cache
	    X509CRL crl2 = crlService.generateCRL(crlProfile);
	    assertEquals(crl1.getThisUpdate().getTime(), crl2.getThisUpdate().getTime());

	    // Crear the cache
	    certificateStatus.setCertificateStatus(CertificateStatusValues.V);
	    certificateStatus = certificateStatusService.saveStatus(certificateStatus);
	    Thread.sleep(1000);

	    // Generate a new CRL and update cache
	    crl1 = crlService.generateCRL(crlProfile);
	    assertNull(crl1.getRevokedCertificate(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER));
	    if (crl1.getThisUpdate().getTime() == crl2.getThisUpdate().getTime())
	    {
		fail("The cache has not been evicted");
	    }
	}
	catch (Exception exc)
	{
	    fail();
	}
    }

    @Test
    public void testCertificateProfileCache()
    {
	String description = "description_";
	certificateProfile = testSupport.generateBaseProfile("testCertificateProfileCache", description + "1");
	BaseCertificateField extendedKeyUsageField = new ExtendedKeyUsageField(certificateProfile, true);
	extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_CODE_SIGNING));
	extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_IPSEC_IKE));
	certificateProfile.addField(extendedKeyUsageField);
	certificateProfile = certificateProfileService.saveProfile(certificateProfile);

	// Cache the profile
	certificateProfile = certificateProfileService.getProfile(certificateProfile);

	// Hit cache
	CertificateProfile certificateProfileCached = certificateProfileService.getProfile(certificateProfile);

	// Compare profiles
	assertEquals(certificateProfile.getProfileId(), certificateProfileCached.getProfileId());
	assertEquals(certificateProfile.getProfileName(), certificateProfileCached.getProfileName());
	assertEquals(certificateProfile.getProfileDescription(), certificateProfileCached.getProfileDescription());
	assertEquals(certificateProfile.getFields().size(), certificateProfileCached.getFields().size());
	assertEquals(((SubjectField) certificateProfile.getField(FieldType.SUBJECT)).getPattern(), ((SubjectField) certificateProfileCached.getField(FieldType.SUBJECT)).getPattern());
	assertEquals(((IssuerField) certificateProfile.getField(FieldType.ISSUER)).getPattern(), ((IssuerField) certificateProfileCached.getField(FieldType.ISSUER)).getPattern());
	assertEquals(certificateProfile.getField(FieldType.EXTENDED_KEY_USAGE).getValues().size(), certificateProfileCached.getField(FieldType.EXTENDED_KEY_USAGE).getValues().size());

	for (int i = 0; i < certificateProfile.getField(FieldType.EXTENDED_KEY_USAGE).getValues().size(); i++)
	{
	    assertEquals(((ExtendedKeyUsageFieldValue) certificateProfile.getField(FieldType.EXTENDED_KEY_USAGE).getValues().get(i)).getExtendedKeyUsage(), ((ExtendedKeyUsageFieldValue) certificateProfileCached.getField(FieldType.EXTENDED_KEY_USAGE).getValues().get(i)).getExtendedKeyUsage());
	}

	// Update profile -> clear cache
	certificateProfile.setProfileDescription(description + "2");
	certificateProfile = certificateProfileService.saveProfile(certificateProfile);

	// Update cache
	certificateProfile = certificateProfileService.getProfile(certificateProfile);
    }

    @Test
    public void testCRLProfileCache()
    {
	String description = "description_";
	crlProfile = testSupport.createCRLProfile(description + "1");
	crlProfileService.saveProfile(crlProfile);

	// Cache the profile
	crlProfile = crlProfileService.getProfile(crlProfile);

	// Hit cache
	CRLProfile crlProfileCached = crlProfileService.getProfile(crlProfile);

	// Compare profiles
	assertEquals(crlProfile.getProfileId(), crlProfileCached.getProfileId());
	assertEquals(crlProfile.getProfileName(), crlProfileCached.getProfileName());
	assertEquals(crlProfile.getProfileDescription(), crlProfileCached.getProfileDescription());
	assertEquals(crlProfile.getFields().size(), crlProfileCached.getFields().size());

	// Update profile -> clear cache
	crlProfile.setProfileDescription(description + 2);
	crlProfile = crlProfileService.saveProfile(crlProfile);

	// Update cache
	crlProfile = crlProfileService.getProfile(crlProfile);
    }

    @After
    public void tearDown()
    {
	if (certificateProfile != null)
	{
	    // If cache does not work OptimisticLockException will be launch
	    certificateProfileService.deleteProfile(certificateProfile);
	}

	if (crlProfile != null)
	{
	    // If cache does not work OptimisticLockException will be launch
	    crlProfileService.deleteProfile(crlProfile);
	}

	if (certificateStatus != null)
	{
	    certificateStatusService.deleteStatus(certificateStatus);
	}
    }

}
