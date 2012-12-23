package org.orinocoX509.service.impl.extensions;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.field.certificate.DeviceTypeField;

public class DeviceTypeExtension implements EngineExtension
{
    private BaseCertificateField certificateField;

    public DeviceTypeExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {
	ASN1ObjectIdentifier deviceTypeOID = new ASN1ObjectIdentifier("1.3.6.1.4.1.15096.1.4.2");
	certificateGenerator.addExtension(deviceTypeOID, certificateField.getCritical(), new DEROctetString(((DeviceTypeField) this.certificateField).getDeviceType().getBytes()));
	return (certificateGenerator);
    }
}
