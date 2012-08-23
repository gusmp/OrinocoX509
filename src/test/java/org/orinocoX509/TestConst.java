package org.orinocoX509;

import java.math.BigInteger;

import org.bouncycastle.util.encoders.Base64;
import org.orinocoX509.entity.CertificateProfile.KeySizeValues;

public class TestConst 
{
	// General for profiles
	public static final String PROFILE_DESCRIPTION     = "profileDescription";
	public static final Integer PROFILE_YEARS          = 4;
	public static final KeySizeValues PROFILE_KEY_SIZE = KeySizeValues.SIZE_1024;
	// Check org\bouncycastle\asn1\x500\style\BCStyle.java for all fields available 
	public static final String SUBJECT_PATTERN         = "C=$country, OU=$ou2, OU=$ou1, SURNAME=$surname, GIVENNAME=$given_name, serialnumber=$serial_number, CN=$cn";
	public static final String ISSUER_PATTERN          = "C=ES, ST=Barcelona, L=Badalona, OU=CA Subordinada de primer nivel, OU=ENTERPRISE.CV, CN=testCA";
	
	
	// Dummy PKCS#10
	public static final String PKCS10RequestTest = "MIIBTzCBuQIBADASMRAwDgYDVQQDDAdzdWJqZWN0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHFw3lRtJdBTgPT+k6USXR9VvtdbV6e8lQbJIXINZ4l26khfIBNa3ok2Bca3TgTmieLISKSJo6mV5FHBXsVJptEzEC5fxrEWzLqG/SUxLOT/tWda9nrJ4jasIoVyIRWxzn7koziRMhs66voEK5rU5sg5BHgOZSOakJUeuvJbqhEwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAD/pvS5YLRZiqvI+UMoEZwhaNB0uKsCSVOHontLyGPhOuoHVg9409eqChMIyfmg9jIaqJT/o9kL+uOxqWyNf9wMmcL+hPDPg/5ynXmG01UvQGl0wYxOq+GbVepJhMRQ4X9Q+wPa1++bvm1k2etMHjbkkau2aXfyuc+Nr5fkdA6tu";
	
	
	// Authority Information Access
	public static final String AIA_OCSP_URL = "http://ocsp.url.com";
	public static final String AIA_CA_URL   = "http://crl.url.com/crl.crl";	
	
	
	// Authority Key Identifier
	// 78 8b 5d 68 94 a7 40 10 ad ad d7 04 13 36 23 b0 b7 fc 91 2a
	public static final byte[] AUTHORITY_KEY_ID  = {120, -117, 93, 104, -108, -89, 64, 16, -83, -83, -41, 4, 19, 54, 35, -80, -73, -4, -111, 42};
	public static final String ROOT_SUBJECT    = "C=ES,ST=Barcelona,L=Badalona,OU=CA Raiz,OU=ENTERPRISE.CV,CN=gusCA";
	
	
	// Certificate Policies
	public static final String POLICY_OID  = "1.2.3.4.5.6";
	public static final String CPS         = "http://wwww.yourUrl.com";
	public static final String USER_NOTICE = "text, text and more text";
	
	
	// CRL Distribution Point
	public static final String CRLDP1_URL1 = "http://crldp1.url.com";
	public static final String CRLDP1_URL2 = "http://crldp1.backup.url.com";
	public static final String CRLDP2_URL1 = "http://crldp2.url.com";
	public static final String CRLDP2_URL2 = "http://crldp2.backup.url.com";
	
	// Device Type
	public static final String DEVICE_TYPE_TEXT = "Device Type";
	
	
	// Extended Key Usage
	
	// Issuer Alternative Name
	public static final String IAN_RFC822NAME_VALUE     = "mail@issuer.com";
	public static final String IAN_DIRECTORY_NAME_VALUE = "CN=common name";
	
	// Key Usage
	
	/*
	 KeyUsage ::= BIT STRING {
           digitalSignature        (0),
           nonRepudiation          (1),
           keyEncipherment         (2),
           dataEncipherment        (3),
           keyAgreement            (4),
           keyCertSign             (5),
           cRLSign                 (6),
           encipherOnly            (7),
           decipherOnly            (8) }
	 */
	public static final boolean  KEY_USAGES[] = {true, false, false, false, false, false, true, false, false};
	
	// Netscape Certificate Type
	
	/*
	 NetscapeCertType ::= BIT STRING {
         SSLClient               (0),
         SSLServer               (1),
         S/MIME                  (2),
         Object Signing          (3),
         Reserved                (4),
         SSL CA                  (5),
         S/MIME CA               (6),
         Object Signing CA       (7) }

	 */
	public static final int NETSCAPE_CERTIFICATE_TYPES = Integer.parseInt("00100001", 2);
	
	// Subject Alternative Name
	public static final String SAN_RFC822NAME_VALUE       = "mail@subject.com";
	public static final String SAN_DIRECTORY_NAME_PATTERN = "C=ES, O=Organization, OU=Organizational Unit, SN=$serial_number, CN=$cn";
	public static final String SAN_DIRECTORY_NAME_VALUE   = "CN=common name,2.5.4.5=#130931323334353637385a,OU=Organizational Unit,O=Organization,C=ES";
	
	// Subject Key Identifier
	// ce df 97 12 66 be ba 1e 5b 67 64 4d 14 e1 ac 9c 30 63 88 d6
	public static final byte[] SUBJECT_KEY_IDENTIFIER = {-50, -33, -105, 18, 102, -66, -70, 30, 91, 103, 100, 77, 20, -31, -84, -100, 48, 99, -120, -42};
	
	// For testing CertificateService
	public static BigInteger DUMMY_CERTIFICATE_SERIAL_NUMBER = new BigInteger("1234");
	
	// For testing CertificateStatusService
	public static BigInteger NON_EXISTING_CERTIFICATE_SERIAL_NUMBER = new BigInteger("2");
	
	// For testing CRLProfile
	public static Integer DAYS_NEXT_UPDATE = 7;
	
	// For testing CRL Service
	public static BigInteger SERIAL_NUMBER_1 = new BigInteger("98");
	public static BigInteger SERIAL_NUMBER_2 = new BigInteger("99");
	public static String CRL_EC_IDCAT_1 = "http://epscd.catcert.net/crl/ec-idcat.crl";
	public static String CRL_EC_IDCAT_2 = "http://epscd2.catcert.net/crl/ec-idcat.crl";
	
	// For testing OcspService
	public static String OCSP_URL = "http://ocsp.catcert.net";
	public static BigInteger SERIAL_NUMBER_VALID = new BigInteger("02ff66",16);
	public static BigInteger SERIAL_NUMBER_REVOKED = new BigInteger("00f1d7",16);
	
	public static byte[] VALID_EC_IDCAT_CERTIFICATE = Base64.decode("" +
			"MIIJXjCCCEagAwIBAgIDAv9mMA0GCSqGSIb3DQEBBQUAMIIBMTELMAkGA1UEBhMC" +
			"RVMxOzA5BgNVBAoTMkFnZW5jaWEgQ2F0YWxhbmEgZGUgQ2VydGlmaWNhY2lvIChO" +
			"SUYgUS0wODAxMTc2LUkpMTQwMgYDVQQHEytQYXNzYXRnZSBkZSBsYSBDb25jZXBj" +
			"aW8gMTEgMDgwMDggQmFyY2Vsb25hMS4wLAYDVQQLEyVTZXJ2ZWlzIFB1YmxpY3Mg" +
			"ZGUgQ2VydGlmaWNhY2lvIEVDVi0yMTUwMwYDVQQLEyxWZWdldSBodHRwczovL3d3" +
			"dy5jYXRjZXJ0Lm5ldC92ZXJDSUMtMiAoYykwMzE1MDMGA1UECxMsRW50aXRhdCBw" +
			"dWJsaWNhIGRlIGNlcnRpZmljYWNpbyBkZSBjaXV0YWRhbnMxETAPBgNVBAMTCEVD" +
			"LUlEQ2F0MB4XDTExMTEyNDE2MjE0M1oXDTE1MTEyMzE2MjE0M1owgfExCzAJBgNV" +
			"BAYTAkVTMTUwMwYDVQQLEyxWZWdldSBodHRwczovL3d3dy5jYXRjZXJ0LmNhdC92" +
			"ZXJpZENBVCAoYykwMzExMC8GA1UECxMoU2VydmVpcyBQdWJsaWNzIGRlIENlcnRp" +
			"ZmljYWNpbyBDUElYU0EtMjEdMBsGA1UEBBQUREUgTEEgUEXHQSBERSBQUk9WRVMx" +
			"FzAVBgNVBCoTDlBFUlNPTkEgRklTSUNBMRIwEAYDVQQFEwkwMDAwMDAwMFQxLDAq" +
			"BgNVBAMUI1BFUlNPTkEgRklTSUNBIERFIExBIFBFx0EgREUgUFJPVkVTMIIBIjAN" +
			"BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjfupY+7O25Qwy/Hr5vr5GGe+3Gz1" +
			"laGvzyrw62L6spiKyDzLr/sUhYHz2QLbzAhTb/aKeX3tnPW1eqFbNHVaan+Hl9cX" +
			"M3wVQX4jKTXQq16kamXVaHORaDay6W6bj9VOGoxh4ZkSa215o7CoI8UtUMUrAxYo" +
			"iCGNWvGsPn3Duw//oQgyKBfbkYvfaWxulec8GOSuIg4EDLuSHgULxumy+JYmapPh" +
			"cvcjk6U6qytoWChz8UPrWHY9NMzpWsixifI6s69Hvja9dZ0CjBVs5L6XdSU7gwoC" +
			"pb4+ZqgTGYHjDcnPDTRCQy7C3/ckLhgMlUy+8/SZQFzWCGNG1dexTK7MfwIDAQAB" +
			"o4IEujCCBLYwDgYDVR0PAQH/BAQDAgXgMIGsBgNVHREEgaQwgaGBEmNvcnJldUBj" +
			"YXRjZXJ0LmNhdKSBijCBhzELMAkGA1UEBhMCRVMxKTAnBgNVBAoTIEFnZW5jaWEg" +
			"Q2F0YWxhbmEgZGUgQ2VydGlmaWNhY2lvMQ4wDAYDVQQLEwVJRENBVDEPMA0GA1UE" +
			"BRMGMDJGRjY2MSwwKgYDVQQDFCNQRVJTT05BIEZJU0lDQSBERSBMQSBQRcdBIERF" +
			"IFBST1ZFUzAfBgNVHRIEGDAWgRRlY19pZGNhdEBjYXRjZXJ0Lm5ldDAdBgNVHQ4E" +
			"FgQUbhZ6LKzxTlIeemSmsjQ+88STK/swggExBgNVHSMEggEoMIIBJIAUzZLARUY0" +
			"dg3S9FuidB2rz2y2C7mhgfmkgfYwgfMxCzAJBgNVBAYTAkVTMTswOQYDVQQKEzJB" +
			"Z2VuY2lhIENhdGFsYW5hIGRlIENlcnRpZmljYWNpbyAoTklGIFEtMDgwMTE3Ni1J" +
			"KTEoMCYGA1UECxMfU2VydmVpcyBQdWJsaWNzIGRlIENlcnRpZmljYWNpbzE1MDMG" +
			"A1UECxMsVmVnZXUgaHR0cHM6Ly93d3cuY2F0Y2VydC5uZXQvdmVyYXJyZWwgKGMp" +
			"MDMxNTAzBgNVBAsTLEplcmFycXVpYSBFbnRpdGF0cyBkZSBDZXJ0aWZpY2FjaW8g" +
			"Q2F0YWxhbmVzMQ8wDQYDVQQDEwZFQy1BQ0OCEHBAQND9yjwZP6I8J2HqcM4wHQYD" +
			"VR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMBEGCWCGSAGG+EIBAQQEAwIFoDBv" +
			"BggrBgEFBQcBAQRjMGEwIwYIKwYBBQUHMAGGF2h0dHA6Ly9vY3NwLmNhdGNlcnQu" +
			"Y2F0MDoGCCsGAQUFBzAChi5odHRwOi8vd3d3LmNhdGNlcnQuY2F0L2Rlc2NhcnJl" +
			"Z2EvZWMtaWRjYXQuY3J0MCUGCCsGAQUFBwEDBBkwFzAIBgYEAI5GAQEwCwYGBACO" +
			"RgEDAgEPMGYGA1UdHwRfMF0wW6BZoFeGKWh0dHA6Ly9lcHNjZC5jYXRjZXJ0Lm5l" +
			"dC9jcmwvZWMtaWRjYXQuY3JshipodHRwOi8vZXBzY2QyLmNhdGNlcnQubmV0L2Ny" +
			"bC9lYy1pZGNhdC5jcmwwgfQGA1UdIASB7DCB6TCB5gYMKwYBBAH1eAEDAVYBMIHV" +
			"MCwGCCsGAQUFBwIBFiBodHRwczovL3d3dy5jYXRjZXJ0LmNhdC92ZXJpZENBVDCB" +
			"pAYIKwYBBQUHAgIwgZcagZRBcXVlc3QgZXMgdW4gY2VydGlmaWNhdCBwZXJzb25h" +
			"bCBpZENBVCwgcmVjb25lZ3V0IGQnaWRlbnRpZmljYWNpbywgc2lnbmF0dXJhIGkg" +
			"eGlmcmF0IGRlIGNsYXNzZSAyIGluZGl2aWR1YWwuIFZlZ2V1IGh0dHBzOi8vd3d3" +
			"LmNhdGNlcnQuY2F0L3ZlcmlkQ0FUMCgGCisGAQQB9XgBBAIEGhMYTm9uLUNyeXB0" +
			"b2dyYXBoaWMgRGV2aWNlMC0GA1UdCQQmMCQwEAYIKwYBBQUHCQQxBBMCRVMwEAYI" +
			"KwYBBQUHCQUxBBMCRVMwDQYJKoZIhvcNAQEFBQADggEBAAOsPoOM/N3LpOozJdF4" +
			"KN2fOG/ESHg8RZUnpx1CE4J8Gw9m7D8rmpGR+onzbZV1QOzZLpPi2kwVS4n7ve/X" +
			"5uH2g2Js0kf1BofMPXyRjTwTJMNW9dKoMH0Ev6rt7xLeBLODaFeG4pfX5bdvI74S" +
			"evBdIqjiU1Jl5B3vM+2N+K8jaalodIjeFKha8kfhKIv1c6/c5Zu46HOqrzKB1iZA" +
			"YEONxSMP+ufb+iRZbkciEGdUTIrKkfzR42NkdYz3a6F7ABOCz82MX32kl9WA8cAK" +
			"eatB1QS41xzFpvDmZSPx5by3M3a0kVfoA2VPB8RmxAo4rCh0iY18KJC6LAIHLQx/" +
			"hhg=");

}
