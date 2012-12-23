package org.orinocoX509.util;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CryptographicUtil
{
    @Autowired
    private Base64Util base64Util;

    public X509Certificate pemToX509Certificate(String certificate) throws CertificateException
    {
	return (derToX509Certificate(base64Util.base64ByteDecode(certificate)));
    }
    
    public X509Certificate derToX509Certificate(byte[] certificate) throws CertificateException
    {

	CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
	return ((X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certificate)));
    }

    public String reverseDN(String dn)
    {
	String[] dnArray = dn.split(",");
	if (dnArray.length < 2)
	{
	    return (dn);
	}
	else
	{
	    StringBuffer reverseDn = new StringBuffer();
	    for (int i = dnArray.length - 1; i >= 0; i--)
	    {
		if (i > 0)
		{
		    reverseDn.append(dnArray[i].trim() + ", ");
		}
		else
		{
		    reverseDn.append(dnArray[i]);
		}
	    }
	    return (reverseDn.toString());
	}
    }
}
