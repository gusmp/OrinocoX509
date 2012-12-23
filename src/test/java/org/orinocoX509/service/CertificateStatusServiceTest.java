package org.orinocoX509.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../applicationContext-Test.xml")
public class CertificateStatusServiceTest
{
    @Autowired
    CertificateStatusService certificateStatusService;

    @Autowired
    TestSupport testSupport;

    private CertificateStatus certificateStatus = null;

    @Before
    public void init()
    {
    }

    @Test
    public void addStatus()
    {
	certificateStatus = testSupport.generateCertificateStatus(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER, CertificateStatusValues.R, null, null);

	certificateStatusService.saveStatus(certificateStatus);
	assertNotNull(certificateStatusService.getStatus(certificateStatus));
    }

    @Test
    public void updateStatus()
    {
	certificateStatus = testSupport.generateCertificateStatus(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER, CertificateStatusValues.R, null, null);
	certificateStatusService.saveStatus(certificateStatus);

	certificateStatus.setCertificateStatus(CertificateStatusValues.V);
	certificateStatusService.saveStatus(certificateStatus);

	CertificateStatus certificateStatus2 = certificateStatusService.getStatus(certificateStatus);
	assertEquals(CertificateStatusValues.V, certificateStatus2.getCertificateStatus());
	assertEquals(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER, certificateStatus2.getCertificateSerialNumber());
    }

    @Test
    public void getStatusForNonExistingCertificate()
    {
	certificateStatus = new CertificateStatus(TestConst.NON_EXISTING_CERTIFICATE_SERIAL_NUMBER);
	certificateStatus = certificateStatusService.getStatus(certificateStatus);
	assertEquals(CertificateStatusValues.U, certificateStatus.getCertificateStatus());
    }

    @Test
    public void expiredStatus()
    {
	Calendar notBefore = GregorianCalendar.getInstance();
	notBefore.add(Calendar.YEAR, -3);
	Calendar notAfter = GregorianCalendar.getInstance();
	notAfter.add(Calendar.YEAR, -2);

	certificateStatus = testSupport.generateCertificateStatus(TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER, CertificateStatusValues.V, notAfter.getTime(), notBefore.getTime());
	certificateStatusService.saveStatus(certificateStatus);
	certificateStatus = certificateStatusService.getStatus(certificateStatus);
	assertEquals(CertificateStatusValues.E, certificateStatus.getCertificateStatus());
    }

    @After
    public void tearDown()
    {
	if (certificateStatus != null)
	{
	    certificateStatusService.deleteStatus(certificateStatus);
	}
    }

}
