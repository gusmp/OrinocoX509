package org.orinocoX509.service;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.util.CryptographicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../applicationContext-Test.xml")
public class OCSPServiceTest
{
    @Autowired
    OCSPService ocspService;

    @Autowired
    CryptographicUtil cryptographicUtil;

    private final byte[] EC_IDCAT = Base64.decode("" + "MIIHsDCCBpigAwIBAgIQcEBA0P3KPBk/ojwnYepwzjANBgkqhkiG9w0BAQUFADCB" + "8zELMAkGA1UEBhMCRVMxOzA5BgNVBAoTMkFnZW5jaWEgQ2F0YWxhbmEgZGUgQ2Vy" + "dGlmaWNhY2lvIChOSUYgUS0wODAxMTc2LUkpMSgwJgYDVQQLEx9TZXJ2ZWlzIFB1" + "YmxpY3MgZGUgQ2VydGlmaWNhY2lvMTUwMwYDVQQLEyxWZWdldSBodHRwczovL3d3" + "dy5jYXRjZXJ0Lm5ldC92ZXJhcnJlbCAoYykwMzE1MDMGA1UECxMsSmVyYXJxdWlh" + "IEVudGl0YXRzIGRlIENlcnRpZmljYWNpbyBDYXRhbGFuZXMxDzANBgNVBAMTBkVD" + "LUFDQzAeFw0wMzEwMzExMDQwMzlaFw0xOTEwMzExMDQwMzlaMIIBMTELMAkGA1UE" + "BhMCRVMxOzA5BgNVBAoTMkFnZW5jaWEgQ2F0YWxhbmEgZGUgQ2VydGlmaWNhY2lv" + "IChOSUYgUS0wODAxMTc2LUkpMTQwMgYDVQQHEytQYXNzYXRnZSBkZSBsYSBDb25j" + "ZXBjaW8gMTEgMDgwMDggQmFyY2Vsb25hMS4wLAYDVQQLEyVTZXJ2ZWlzIFB1Ymxp"
	    + "Y3MgZGUgQ2VydGlmaWNhY2lvIEVDVi0yMTUwMwYDVQQLEyxWZWdldSBodHRwczov" + "L3d3dy5jYXRjZXJ0Lm5ldC92ZXJDSUMtMiAoYykwMzE1MDMGA1UECxMsRW50aXRh" + "dCBwdWJsaWNhIGRlIGNlcnRpZmljYWNpbyBkZSBjaXV0YWRhbnMxETAPBgNVBAMT" + "CEVDLUlEQ2F0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu99Aw9Qc" + "EXkizypUs83y67eERM1LSEGeAUeA0eGAYDFpya7tKa6p76vXYO0pXcDydLV5V1ZK" + "CzPstVJWNBBMtrBczAPIX59p1cbHkCcXv/ImACXjTUIkIOppy4C2jvQA9/0Nmag8" + "PgSCnk+47msKIqncl/p74fQI8kyoucjJPF5ffYvAdgrSoLA6GDGIG+0maN0x+ydD" + "VMqfpheO7GXnuv9FALEbZfnseq7FpkoXM830bUlnxA63GAEmPeSs0S1JEW6lhk2z" + "RsPuJMX2h99Xyf+lUnpDzP6INWftEP4g1S0smb6JHFBBxTIrXI36UQJkas5hfROD" + "7XhCm/2WrKvrWQIDAQABo4IC/TCCAvkwHQYDVR0SBBYwFIESZWNfYWNjQGNhdGNl" + "cnQubmV0MB8GA1UdEQQYMBaBFGVjX2lkY2F0QGNhdGNlcnQubmV0MBIGA1UdEwEB"
	    + "/wQIMAYBAf8CAQAwDgYDVR0PAQH/BAQDAgEGMB0GA1UdDgQWBBTNksBFRjR2DdL0" + "W6J0HavPbLYLuTCCATEGA1UdIwSCASgwggEkgBSgw4tEqjelRb+XgFrR8Xiim+ld" + "jaGB+aSB9jCB8zELMAkGA1UEBhMCRVMxOzA5BgNVBAoTMkFnZW5jaWEgQ2F0YWxh" + "bmEgZGUgQ2VydGlmaWNhY2lvIChOSUYgUS0wODAxMTc2LUkpMSgwJgYDVQQLEx9T" + "ZXJ2ZWlzIFB1YmxpY3MgZGUgQ2VydGlmaWNhY2lvMTUwMwYDVQQLEyxWZWdldSBo" + "dHRwczovL3d3dy5jYXRjZXJ0Lm5ldC92ZXJhcnJlbCAoYykwMzE1MDMGA1UECxMs" + "SmVyYXJxdWlhIEVudGl0YXRzIGRlIENlcnRpZmljYWNpbyBDYXRhbGFuZXMxDzAN" + "BgNVBAMTBkVDLUFDQ4IQ7is969Qh3hSoYqwE893EATCB2gYDVR0gBIHSMIHPMIHM" + "BgsrBgEEAfV4AQMBDDCBvDAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cuY2F0Y2Vy" + "dC5uZXQvdmVyQ0lDLTIwgYsGCCsGAQUFBwICMH8afUFxdWVzdCBjZXJ0aWZpY2F0" + "IHOSZW1ldCD6bmljYSBpIGV4Y2x1c2l2YW1lbnQgYSBFbnRpdGF0cyBkZSBDZXJ0"
	    + "aWZpY2FjafMgZGUgQ2xhc3NlIDIuIFZlZ2V1IGh0dHBzOi8vd3d3LmNhdGNlcnQu" + "bmV0L3ZlckNJQy0yMGIGA1UdHwRbMFkwV6BVoFOGJ2h0dHA6Ly9lcHNjZC5jYXRj" + "ZXJ0Lm5ldC9jcmwvZWMtYWNjLmNybIYoaHR0cDovL2Vwc2NkMi5jYXRjZXJ0Lm5l" + "dC9jcmwvZWMtYWNjLmNybDANBgkqhkiG9w0BAQUFAAOCAQEAGUdf7hMDZiiT9fHL" + "VyeziEJHI4KazVHUNRuyrxWsUTPDBHwOOO6KtAElKbyxKwh8SX+nJwlGVZXwYgZX" + "Su0zyJp3v+pcXwSrRPmovsD7C8JsyrJnaMw7zqTz55Rc8GBEyoznflRkl3MiNONV" + "iLD/rl30G+JN/gwExVFA9n/3noXR2Hmi+FqlE/btZ+xoT1QJGbgjH3qAg7DbAYh2" + "DOQ2Dy9F5i5/Y5KXwEypesNqTj8duwFGeBEUJrV2iJqKbc76sKulQ3rdSo6Npo0k" + "ZPRbdKn10+xcpp91I58tgBNWZRTxPVig0JvRAudFQCxY4PF5WSWqz7GaMUm2Nhqw" + "8jpmNA==");

    private X509Certificate EC_AL_X509_CERTIFICATE;

    @Before
    public void init()
    {
	try
	{
	    ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(EC_IDCAT);
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    EC_AL_X509_CERTIFICATE = (X509Certificate) cf.generateCertificate(bArrayInputStream);
	    bArrayInputStream.close();
	}
	catch (Exception exc)
	{
	    fail();
	}
    }

    @Test
    public void emptyTest()
    {
    }

    // These tests require Internet connection

    // @Test
    public void requestValidCertificate()
    {
	CertificateStatusValues status = ocspService.getStatus(EC_AL_X509_CERTIFICATE, TestConst.SERIAL_NUMBER_VALID, TestConst.OCSP_URL);
	assertEquals(CertificateStatusValues.V, status);
    }

    // @Test
    public void requestRevokedCertificate()
    {
	CertificateStatusValues status = ocspService.getStatus(EC_AL_X509_CERTIFICATE, TestConst.SERIAL_NUMBER_REVOKED, TestConst.OCSP_URL);
	assertEquals(CertificateStatusValues.R, status);
    }

    @After
    public void tearDown()
    {
    }

}
