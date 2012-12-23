package org.orinocoX509.service.impl.extensions;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue;
import org.orinocoX509.entity.value.certificate.NetscapeCertificateTypeFieldValue.NetscapeCertificateTypeType;

public class NetscapeCertificateTypeExtension implements EngineExtension
{
    private BaseCertificateField certificateField;

    public NetscapeCertificateTypeExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {
	int netscapeTypes = 0;
	for (BaseCertificateFieldValue value : certificateField.getValues())
	{
	    NetscapeCertificateTypeFieldValue netscapeCertificateTypeValue = (NetscapeCertificateTypeFieldValue) value;

	    if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.OBJECT_SIGNING)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.objectSigning;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.OBJECT_SIGNING_CA)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.objectSigningCA;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.RESERVED)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.reserved;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.SMIME)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.smime;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.SMIME_CA)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.smimeCA;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.SSL_CA)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.sslCA;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.SSL_CLIENT)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.sslClient;
	    }
	    else if (netscapeCertificateTypeValue.getNetscapeCertificateType() == NetscapeCertificateTypeType.SSL_SERVER)
	    {
		netscapeTypes = netscapeTypes | NetscapeCertType.sslServer;
	    }
	}

	certificateGenerator.addExtension(MiscObjectIdentifiers.netscapeCertType, certificateField.getCritical(), new NetscapeCertType(netscapeTypes));
	return (certificateGenerator);
    }
}
