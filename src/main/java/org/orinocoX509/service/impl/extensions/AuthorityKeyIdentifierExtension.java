package org.orinocoX509.service.impl.extensions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;
import org.orinocoX509.entity.value.certificate.AuthorityKeyIdentifierFieldValue.AuthorityKeyIdentifierType;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;

public class AuthorityKeyIdentifierExtension implements EngineExtension
{
    private BaseCertificateField certificateField;
    private PublicKey caPublicKey;
    private X509Certificate caCertificate;

    public AuthorityKeyIdentifierExtension(BaseCertificateField certificateField, PublicKey caPublicKey, X509Certificate caCertificate)
    {
	this.certificateField = certificateField;
	this.caPublicKey = caPublicKey;
	this.caCertificate = caCertificate;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator)
    {
	try
	{
	    AuthorityKeyIdentifier authorityKeyId = null;
	    List<BaseCertificateFieldValue> akiFieldValues = certificateField.getValues();
	    SubjectPublicKeyInfo subjectPublicKeyInfo = null;
	    GeneralName genName = null;

	    for (int i = 0; i < akiFieldValues.size(); i++)
	    {
		AuthorityKeyIdentifierFieldValue akiFieldValue = (AuthorityKeyIdentifierFieldValue) akiFieldValues.get(i);

		if (akiFieldValue.getAuthorityKeyIdentifier() == AuthorityKeyIdentifierType.KEY_IDENTIFIER)
		{
		    subjectPublicKeyInfo = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(caPublicKey.getEncoded())).readObject());
		}
		else if (akiFieldValue.getAuthorityKeyIdentifier() == AuthorityKeyIdentifierType.AUTH_CERT_ISSUER_AUTH_CERT_SERIAL_NUMBER)
		{
		    genName = new GeneralName(new X500Name(caCertificate.getIssuerDN().getName()));
		}
	    }

	    if ((subjectPublicKeyInfo != null) && (genName != null))
	    {
		authorityKeyId = new AuthorityKeyIdentifier(subjectPublicKeyInfo, new GeneralNames(genName), caCertificate.getSerialNumber());
	    }
	    else if (subjectPublicKeyInfo != null)
	    {
		authorityKeyId = new AuthorityKeyIdentifier(subjectPublicKeyInfo);
	    }
	    else if (genName != null)
	    {
		authorityKeyId = new AuthorityKeyIdentifier(new GeneralNames(genName), caCertificate.getSerialNumber());
	    }

	    if (authorityKeyId != null)
	    {
		certificateGenerator.addExtension(X509Extension.authorityKeyIdentifier, certificateField.getCritical(), authorityKeyId);
	    }
	}
	catch (IOException exc)
	{
	    throw new EngineException(EngineErrorCodes.IO_ERROR, "The public key from the CA public key can not be accessed." + exc.getMessage());
	}

	return (certificateGenerator);
    }
}
