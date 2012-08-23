package org.orinocoX509.cache;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import java.security.cert.X509CRL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.entity.field.certificate.ExtendedKeyUsageField;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue.ExtendedKeyUsageType;
import org.orinocoX509.service.CRLProfileService;
import org.orinocoX509.service.CRLService;
import org.orinocoX509.service.CertificateProfileService;
import org.orinocoX509.service.CertificateStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class LocalCacheTest 
{

	@Autowired
	CRLProfileService crlProfileService;
	
	@Autowired
	CertificateStatusService certificateStatusService;
	
	@Autowired
	TestSupport testSupport;
	
	@Autowired
	CRLService crlService;
	
	@Autowired
	CertificateProfileService certificateProfileService;
	
	private CertificateProfile certificateProfile = null;
	private CRLProfile crlProfile = null;
	private CertificateStatus certificateStatus = null;
	
	@Before
	public void init() { }
	
	@Test
	public void testCRLCache()
	{
		crlProfile =  testSupport.createCRLProfile("testCRLLocalCache");
		
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
		catch(Exception exc)
		{
			fail();
		}
	}
	
	@Test
	public void testCertificateProfileCache()
	{
		String description = "description_";
		certificateProfile = testSupport.generateBaseProfile("testCertificateProfileCache", description+"1");
		CertificateField extendedKeyUsageField = new ExtendedKeyUsageField(certificateProfile, true);
		extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_CODE_SIGNING));	
		extendedKeyUsageField.addValue(new ExtendedKeyUsageFieldValue(ExtendedKeyUsageType.ID_KP_IPSEC_IKE));
		certificateProfile.addField(extendedKeyUsageField);
		certificateProfile = certificateProfileService.saveProfile(certificateProfile);
		
		// Cache the profile
		certificateProfile = certificateProfileService.getProfile(certificateProfile);
		
		// Hit cache
		CertificateProfile certificateProfileCached = certificateProfileService.getProfile(certificateProfile);
		assertEquals(certificateProfile, certificateProfileCached);
		
		// Update profile -> clear cache
		certificateProfile.setProfileDescription(description+"2");
		certificateProfile = certificateProfileService.saveProfile(certificateProfile);
		
		// Update cache
		certificateProfile = certificateProfileService.getProfile(certificateProfile);
	}
	
	@Test
	public void testCRLProfileCache()
	{
		String description = "description_";
		crlProfile = testSupport.createCRLProfile(description+"1");
		crlProfileService.saveProfile(crlProfile);
		
		// Cache the profile
		crlProfile = crlProfileService.getProfile(crlProfile);
		
		// Hit cache
		CRLProfile crlProfileCached = crlProfileService.getProfile(crlProfile);
		assertEquals(crlProfile, crlProfileCached);
		
		// Update profile -> clear cache
		crlProfile.setProfileDescription(description+2);
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
