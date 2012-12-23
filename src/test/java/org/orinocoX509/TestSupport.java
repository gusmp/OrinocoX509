package org.orinocoX509;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orinocoX509.bean.CertificateValuesBean;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.entity.field.certificate.IssuerField;
import org.orinocoX509.entity.field.certificate.SubjectField;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;
import org.orinocoX509.entity.value.certificate.IssuerFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectFieldValue;
import org.orinocoX509.service.CertificateStatusService;
import org.orinocoX509.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSupport
{

    @Autowired
    private Base64Util base64Util;

    public CertificateProfile createEmptyProfile(String profileName, String profileDescription)
    {
	return (new CertificateProfile(profileName, profileDescription, TestConst.PROFILE_YEARS, TestConst.PROFILE_KEY_SIZE));
    }

    public CertificateProfile generateBaseProfile(String profileName, String profileDescription)
    {
	CertificateProfile profile = new CertificateProfile(profileName, profileDescription, TestConst.PROFILE_YEARS, TestConst.PROFILE_KEY_SIZE);
	BaseCertificateField subject = new SubjectField(profile, TestConst.SUBJECT_PATTERN);
	BaseCertificateField issuer = new IssuerField(profile, TestConst.ISSUER_PATTERN);
	profile.addField(subject);
	profile.addField(issuer);

	return (profile);
    }

    public CertificateValuesBean generateBaseValues()
    {
	CertificateValuesBean values = new CertificateValuesBean();
	values.setRequest(TestConst.PKCS10RequestTest);

	Map<FieldType, Map<String, String>> mapValues = new HashMap<FieldType, Map<String, String>>(3);
	Map<String, String> subjectValues = new HashMap<String, String>(7);

	subjectValues.put("country", "ES");
	subjectValues.put("ou2", "My Organizational Unit 2");
	subjectValues.put("ou1", "My Organizational Unit 1");
	subjectValues.put("surname", "surname");
	subjectValues.put("given_name", "name");
	subjectValues.put("serial_number", "00000000H");
	subjectValues.put("cn", "name surname");

	mapValues.put(FieldType.SUBJECT, subjectValues);
	values.setValues(mapValues);

	return (values);
    }

    public String getValue(BaseCertificateFieldValue fieldValue, boolean isKey, boolean isSubject)
    {
	if (isSubject == true)
	{
	    if (isKey == true)
	    {
		return (((SubjectFieldValue) fieldValue).getPatternKey());
	    }
	    else
	    {
		return (((SubjectFieldValue) fieldValue).getPatternValue());
	    }
	}
	else
	{
	    if (isKey == true)
	    {
		return (((IssuerFieldValue) fieldValue).getPatternKey());
	    }
	    else
	    {
		return (((IssuerFieldValue) fieldValue).getPatternValue());
	    }
	}
    }

    public void checkFieldValue(List<BaseCertificateFieldValue> certificateFieldValues, List<String> expectedValues, boolean isKey, boolean isSubject)
    {
	boolean contain;

	for (int i = 0; i < expectedValues.size(); i++)
	{
	    contain = false;

	    for (int u = 0; u < certificateFieldValues.size(); u++)
	    {
		String value = getValue(certificateFieldValues.get(u), isKey, isSubject);

		if (value.equalsIgnoreCase(expectedValues.get(i)) == true)
		{
		    contain = true;
		    break;
		}
	    }

	    if (contain == false)
	    {
		fail();
	    }
	}
    }

    public void saveCertificate(String name, String content)
    {
	String HEADER = "-----BEGIN CERTIFICATE-----\n";
	String FOOTER = "\n-----END CERTIFICATE-----\n";

	try
	{
	    FileOutputStream fout = new FileOutputStream(name);
	    fout.write(HEADER.getBytes());
	    fout.write(content.getBytes());
	    fout.write(FOOTER.getBytes());
	    fout.close();
	}
	catch (Exception exc)
	{
	    System.out.println("Exc: " + exc.toString());
	}
    }

    public void saveFile(String name, byte[] content)
    {
	try
	{
	    FileOutputStream fout = new FileOutputStream(name);
	    fout.write(content);
	    fout.close();
	}
	catch (Exception exc)
	{
	    System.out.println("Exc: " + exc.toString());
	}
    }

    public X509Certificate getCertificate(String pemCertificate)
    {
	try
	{
	    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
	    return ((X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(base64Util.base64ByteDecode(pemCertificate))));
	}
	catch (Exception exc)
	{
	    return (null);
	}
    }

    public boolean compareArray(boolean param1[], boolean param2[])
    {
	if (param1.length != param2.length)
	{
	    return (false);
	}

	for (int i = 0; i < param1.length; i++)
	{
	    if (param1[i] != param2[i])
	    {
		return (false);
	    }
	}

	return (true);
    }

    public CRLProfile createCRLProfile(String profileName)
    {
	CRLProfile crlProfile = new CRLProfile(profileName, TestConst.PROFILE_DESCRIPTION);
	return (crlProfile);
    }

    public CertificateStatus generateCertificateStatus(BigInteger serialNumber, CertificateStatusValues certificateStatusValue, Date notAfter, Date notBefore)
    {
	if (serialNumber == null)
	{
	    serialNumber = TestConst.DUMMY_CERTIFICATE_SERIAL_NUMBER;
	}

	if (notAfter == null)
	{
	    Calendar calendar = GregorianCalendar.getInstance();
	    calendar.add(Calendar.YEAR, 1);
	    notAfter = calendar.getTime();
	}

	return (new CertificateStatus(serialNumber, certificateStatusValue, notAfter, notBefore));
    }

    public void cleanCRLCache(CertificateStatusService certificateStatusService)
    {
	CertificateStatus certificateStatus = generateCertificateStatus(null, CertificateStatusValues.V, null, null);
	certificateStatusService.saveStatus(certificateStatus);
	certificateStatusService.deleteStatus(certificateStatus);
    }

    /*
     * public CRLProfile generateBaseCRLProfile(String profileName, String
     * profileDescription) { CRLProfile profile = new CRLProfile(profileName,
     * profileDescription, TestConst.MAX_DAYS_REVOKED_CERTIFICATES);
     * return(profile); }
     */

}
