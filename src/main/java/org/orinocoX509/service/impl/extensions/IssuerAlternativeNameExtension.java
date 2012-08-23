package org.orinocoX509.service.impl.extensions;

import java.util.List;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue;
import org.orinocoX509.entity.value.certificate.CertificateFieldValue;
import org.orinocoX509.entity.value.certificate.AlternativeNameFieldValue.AlternativeNameType;


public class IssuerAlternativeNameExtension implements EngineExtension
{
	private CertificateField certificateField;
	
	public IssuerAlternativeNameExtension(CertificateField certificateField)
	{
		this.certificateField = certificateField;
	}
	
	private GeneralName[] prepareAltNameExtensions(List<CertificateFieldValue> altNameFields)
	{
		GeneralName[] gn = new GeneralName[altNameFields.size()]; 
		
		for(int i=0; i < altNameFields.size(); i++)
		{
			AlternativeNameFieldValue alternativeNameEntry = (AlternativeNameFieldValue) altNameFields.get(i);
			
			if (alternativeNameEntry.getAlternativeNameType() == AlternativeNameType.RFC822NAME)
			{
				gn[i] = new GeneralName(GeneralName.rfc822Name, alternativeNameEntry.getValue());
			}
			else if (alternativeNameEntry.getAlternativeNameType() == AlternativeNameType.DIRECTORY_NAME)
			{
				gn[i] = new GeneralName(GeneralName.directoryName, alternativeNameEntry.getValue());
			}
		}
		return(gn);
	}

	public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
	{
		certificateGenerator.addExtension(X509Extension.issuerAlternativeName,
				certificateField.getCritical(),
				new GeneralNames(prepareAltNameExtensions(certificateField.getValues())));
		return(certificateGenerator);
	}
}
