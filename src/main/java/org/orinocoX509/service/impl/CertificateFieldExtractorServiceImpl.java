package org.orinocoX509.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.CertificateFieldExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CertificateFieldExtractorServiceImpl implements CertificateFieldExtractorService
{

    private static final Logger log = LoggerFactory.getLogger(CertificateFieldExtractorServiceImpl.class);

    @Override
    public String getOcspUrl(X509Certificate certificate) throws EngineException
    {
	String ocsp_url = null;
	byte[] aiaExtensionDER = certificate.getExtensionValue("1.3.6.1.5.5.7.1.1");
	ASN1InputStream asn1InputStream = null;

	if (aiaExtensionDER == null)
	{
	    throw new EngineException(EngineErrorCodes.NO_OCSP_URL_AVAILABLE, "The certificate has no Authority information access");
	}

	try
	{
	    DEROctetString derObjectString = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(aiaExtensionDER)).readObject());
	    asn1InputStream = new ASN1InputStream(derObjectString.getOctets());
	    ASN1Sequence asn1Sequence = (ASN1Sequence) asn1InputStream.readObject();
	    asn1InputStream.close();
	    AccessDescription accessDescription = null;

	    for (int i = 0; i < asn1Sequence.size(); i++)
	    {
		accessDescription = AccessDescription.getInstance((ASN1Sequence) asn1Sequence.getObjectAt(i).toASN1Primitive());
		if (accessDescription.getAccessMethod().toString().equalsIgnoreCase("1.3.6.1.5.5.7.48.1") == true)
		{
		    GeneralName gn = accessDescription.getAccessLocation();
		    ocsp_url = gn.getName().toString();
		    break;
		}
	    }

	    if (ocsp_url == null)
	    {
		throw new EngineException(EngineErrorCodes.NO_OCSP_URL_AVAILABLE, "The certificate has AIA extenson but no OCSP could be found");
	    }

	    return (ocsp_url);
	}
	catch (IOException exc)
	{
	    throw new EngineException(EngineErrorCodes.IO_ERROR, "It was an error while getting extension in order to get the OCSP url.\n" + exc.toString());
	}
	finally
	{
	    if (asn1InputStream != null)
	    {
		try
		{
		    asn1InputStream.close();
		}
		catch (IOException excIO)
		{
		}
	    }
	}
    }

    @Override
    public List<String> getCRLDistributionsPoints(X509Certificate certificate) throws EngineException
    {
	List<String> crldpList = new ArrayList<String>(5);
	byte[] crldpExtension = certificate.getExtensionValue("2.5.29.31");
	ASN1InputStream asn1InputStream = null;

	if (crldpExtension == null)
	{
	    log.error("The certificate with serial number " + certificate.getSerialNumber() + " has not CRL Distribution Points");
	    throw new EngineException(EngineErrorCodes.NO_CRL_DP_URL_AVAILABLE, "The certificate with serial number " + certificate.getSerialNumber() + " has not CRL Distribution Points");
	}

	try
	{
	    DEROctetString derOctetString = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(crldpExtension)).readObject());
	    asn1InputStream = new ASN1InputStream(derOctetString.getOctets());
	    ASN1Sequence seq = (ASN1Sequence) asn1InputStream.readObject();
	    asn1InputStream.close();

	    for (int i = 0; i < seq.size(); i++)
	    {
		DistributionPoint crldp = new DistributionPoint((ASN1Sequence) seq.getObjectAt(i).toASN1Primitive());
		GeneralNames generalNames = (GeneralNames) crldp.getDistributionPoint().getName();
		for (int j = 0; j < generalNames.getNames().length; j++)
		{
		    crldpList.add(generalNames.getNames()[j].getName().toString());
		}
	    }

	    return (crldpList);
	}
	catch (IOException exc)
	{
	    log.error("Error reading CRL Distribution point extension.\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.IO_ERROR, "Error reading CRL Distribution point extension.\nDetails:\n" + exc.toString());
	}
	finally
	{
	    if (asn1InputStream != null)
	    {
		try
		{
		    asn1InputStream.close();
		}
		catch (IOException excIO)
		{
		}
	    }
	}
    }

}
