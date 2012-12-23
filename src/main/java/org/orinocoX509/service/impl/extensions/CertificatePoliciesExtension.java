package org.orinocoX509.service.impl.extensions;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.PolicyQualifierId;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.UserNotice;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.field.certificate.CertificatePolicyField;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue;
import org.orinocoX509.entity.value.certificate.CertificatePolicyFieldValue.CertificatePolicyType;

public class CertificatePoliciesExtension implements EngineExtension
{

    private BaseCertificateField certificateField;

    public CertificatePoliciesExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {
	ASN1EncodableVector certificatePolicies = new ASN1EncodableVector();

	ASN1EncodableVector qualifiers = new ASN1EncodableVector();
	CertificatePolicyField certificatePolicyField = (CertificatePolicyField) certificateField;

	for (int i = 0; i < certificatePolicyField.getValues().size(); i++)
	{
	    CertificatePolicyFieldValue certificatePolicyFieldValue = (CertificatePolicyFieldValue) certificatePolicyField.getValues().get(i);
	    if (certificatePolicyFieldValue.getCertificatePolicyType() == CertificatePolicyType.CPS)
	    {
		PolicyQualifierInfo pCps = new PolicyQualifierInfo(certificatePolicyFieldValue.getCertificatePolicy());
		qualifiers.add(pCps);
	    }
	    else if (certificatePolicyFieldValue.getCertificatePolicyType() == CertificatePolicyType.USER_NOTICE)
	    {
		UserNotice userNotice = new UserNotice(null, new DisplayText(DisplayText.CONTENT_TYPE_BMPSTRING, certificatePolicyFieldValue.getCertificatePolicy()));
		PolicyQualifierInfo pUserNotice = new PolicyQualifierInfo(PolicyQualifierId.id_qt_unotice, userNotice);
		qualifiers.add(pUserNotice);
	    }
	}

	if (qualifiers.size() > 0)
	{
	    certificatePolicies.add(new PolicyInformation(new ASN1ObjectIdentifier(certificatePolicyField.getPolicyOid()), new DERSequence(qualifiers)));

	    certificateGenerator.addExtension(X509Extension.certificatePolicies, certificateField.getCritical(), new DERSequence(certificatePolicies));
	}

	return (certificateGenerator);
    }
}
