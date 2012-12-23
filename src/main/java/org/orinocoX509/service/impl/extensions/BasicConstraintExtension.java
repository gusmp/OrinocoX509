package org.orinocoX509.service.impl.extensions;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BasicConstraintField;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;

public class BasicConstraintExtension implements EngineExtension
{
    private BaseCertificateField certificateField;

    public BasicConstraintExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {

	BasicConstraints bc = null;

	if (((BasicConstraintField) certificateField).getIsCA() == false)
	{
	    bc = new BasicConstraints(false);
	}
	else
	{
	    bc = new BasicConstraints(((BasicConstraintField) certificateField).getPathLength());
	}

	certificateGenerator.addExtension(X509Extension.basicConstraints, certificateField.getCritical(), bc);
	return (certificateGenerator);
    }
}
