package org.orinocoX509.service.impl.extensions;

import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.CertificateFieldValue;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue.AlternativeNameType;

public class SubjectAlternativeNameExtension implements EngineExtension
{
	private CertificateField certificateField;
	private Map<String,String> certificateValues;
	private VelocitySupport velocitySupport;
	
	public SubjectAlternativeNameExtension(CertificateField certificateField, Map<String,String> certificateValues, VelocitySupport velocitySupport)
	{
		this.certificateField = certificateField;
		this.certificateValues = certificateValues;
		this.velocitySupport = velocitySupport;
	}
	
	private GeneralName[] prepareAltNameExtensions(List<CertificateFieldValue> altNameFields, Map<String,String> mapValues, String logTemplate)
	{
		GeneralName[] gn = new GeneralName[altNameFields.size()]; 
		
		for(int i=0; i < altNameFields.size(); i ++)
		{
			AlternativeNameFieldValue alternativeNameEntry = (AlternativeNameFieldValue) altNameFields.get(i);
			
			if (alternativeNameEntry.getAlternativeNameType() == AlternativeNameType.RFC822NAME)
			{
				gn[i] = new GeneralName(GeneralName.rfc822Name, mapValues.get("RFC822NAME"));
			}
			else if (alternativeNameEntry.getAlternativeNameType() == AlternativeNameType.DIRECTORY_NAME)
			{
				gn[i] = new GeneralName(GeneralName.directoryName, velocitySupport.applyTemplate(alternativeNameEntry.getValue(), mapValues, logTemplate));
			}
		}
		return(gn);
	}

	public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
	{
		certificateGenerator.addExtension(X509Extension.subjectAlternativeName,
				certificateField.getCritical(),
				new GeneralNames(prepareAltNameExtensions(certificateField.getValues(), certificateValues, "SubjectalternativeName")));
		return(certificateGenerator);
	}
}
