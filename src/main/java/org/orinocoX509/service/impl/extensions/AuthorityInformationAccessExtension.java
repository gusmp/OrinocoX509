package org.orinocoX509.service.impl.extensions;

import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;
import org.orinocoX509.entity.value.certificate.AuthorityInformationAccessFieldValue.AIAType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorityInformationAccessExtension implements EngineExtension
{
    private BaseCertificateField certificateField;
    private static final Logger log = LoggerFactory.getLogger(AuthorityInformationAccessExtension.class);

    public AuthorityInformationAccessExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {
	ASN1EncodableVector listAIA = new ASN1EncodableVector();
	String url;
	String oid = "";

	List<BaseCertificateFieldValue> aiaFieldValues = certificateField.getValues();

	for (int i = 0; i < aiaFieldValues.size(); i++)
	{
	    AuthorityInformationAccessFieldValue aiaFieldValue = (AuthorityInformationAccessFieldValue) aiaFieldValues.get(i);

	    url = aiaFieldValue.getAiaValue();
	    if (aiaFieldValue.getAiaType() == AIAType.ID_AD_CA_ISSUERS)
	    {
		oid = "1.3.6.1.5.5.7.48.2";
	    }
	    else if (aiaFieldValue.getAiaType() == AIAType.ID_AD_OCSP)
	    {
		oid = "1.3.6.1.5.5.7.48.1";
	    }

	    log.debug("AIA Extension. Adding oid " + oid + " (" + aiaFieldValue.getAiaType().name() + ") with value " + url);

	    listAIA.add(new AccessDescription(new ASN1ObjectIdentifier(oid), new GeneralName(GeneralName.uniformResourceIdentifier, url)));
	}

	if (listAIA.size() > 0)
	{
	    certificateGenerator.addExtension(X509Extension.authorityInfoAccess, certificateField.getCritical(), new DERSequence(listAIA));
	}

	return (certificateGenerator);
    }
}
