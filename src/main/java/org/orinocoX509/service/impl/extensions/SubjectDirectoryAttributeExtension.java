package org.orinocoX509.service.impl.extensions;

import java.util.List;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.SubjectDirectoryAttributes;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.value.certificate.CertificateFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue;
import org.orinocoX509.entity.value.certificate.SubjectDirectoryAttributeFieldValue.SubjectDirectoryAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SubjectDirectoryAttributeExtension implements EngineExtension
{
	private List<CertificateFieldValue> values;
	private Boolean critical;
	private static final Logger log = LoggerFactory.getLogger(SubjectDirectoryAttributeExtension.class);
	
	public SubjectDirectoryAttributeExtension(List<CertificateFieldValue> values, Boolean critical)
	{
		this.values = values;
		this.critical = critical;
	}

	public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException 
	{

		Vector<Attribute> attributeVector = new Vector<Attribute>(this.values.size());
		for(int i=0; i < this.values.size(); i++)
		{
			SubjectDirectoryAttributeFieldValue sdaFieldValue = (SubjectDirectoryAttributeFieldValue) this.values.get(i);
			
			if (sdaFieldValue.getSubjectDirectoryAttribute() == SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE)
			{
				Attribute atribute = new Attribute(new ASN1ObjectIdentifier(SubjectDirectoryAttributeType.COUNTRY_OF_RESIDENCE.toString()), 
						new DERSet(new DERPrintableString(sdaFieldValue.getValue())));
				attributeVector.add(atribute);
			}
			else if (sdaFieldValue.getSubjectDirectoryAttribute() == SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP)
			{
				Attribute atribute = new Attribute(new ASN1ObjectIdentifier(SubjectDirectoryAttributeType.COUNTRY_OF_CITIZENSHIP.toString()), 
						new DERSet(new DERPrintableString(sdaFieldValue.getValue())));
				attributeVector.add(atribute);
			}
		}
		
		if (attributeVector.isEmpty() == false)
		{
			log.debug("Number of SubjectDirectoryAttribute to apply " + attributeVector.size() + " Critical: " + critical.booleanValue());
			SubjectDirectoryAttributes sda = new SubjectDirectoryAttributes(attributeVector);
			certificateGenerator.addExtension(X509Extension.subjectDirectoryAttributes, critical, sda);
		}
		
		return(certificateGenerator);
	}
}
