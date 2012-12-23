package org.orinocoX509.service.impl.extensions;

import java.util.Vector;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;

import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.ExtendedKeyUsageFieldValue.ExtendedKeyUsageType;

public class ExtendedKeyUsageExtension implements EngineExtension
{
    private BaseCertificateField certificateField;

    public ExtendedKeyUsageExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {
	Vector<KeyPurposeId> purposeVector = new Vector<KeyPurposeId>(certificateField.getValues().size());
	for (BaseCertificateFieldValue value : certificateField.getValues())
	{
	    ExtendedKeyUsageFieldValue extendedKeyUsageValue = (ExtendedKeyUsageFieldValue) value;
	    if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_CLIENT_AUTH)
	    {
		purposeVector.add(KeyPurposeId.id_kp_clientAuth);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_CODE_SIGNING)
	    {
		purposeVector.add(KeyPurposeId.id_kp_codeSigning);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_EMAIL_PROTECTION)
	    {
		purposeVector.add(KeyPurposeId.id_kp_emailProtection);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_OCSP_SIGNING)
	    {
		purposeVector.add(KeyPurposeId.id_kp_OCSPSigning);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_SERVER_AUTH)
	    {
		purposeVector.add(KeyPurposeId.id_kp_serverAuth);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_TIME_STAMPING)
	    {
		purposeVector.add(KeyPurposeId.id_kp_timeStamping);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_SMARTCARD_LOGON)
	    {
		purposeVector.add(KeyPurposeId.id_kp_smartcardlogon);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_CAP_WAP_AC)
	    {
		purposeVector.add(KeyPurposeId.id_kp_capwapAC);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_CAPWAPWTP)
	    {
		purposeVector.add(KeyPurposeId.id_kp_capwapWTP);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_DVCS)
	    {
		purposeVector.add(KeyPurposeId.id_kp_dvcs);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_EAP_OVER_LAN)
	    {
		purposeVector.add(KeyPurposeId.id_kp_eapOverLAN);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_EAP_OVER_PPP)
	    {
		purposeVector.add(KeyPurposeId.id_kp_eapOverPPP);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_IPSEC_ENDSYSTEM)
	    {
		purposeVector.add(KeyPurposeId.id_kp_ipsecEndSystem);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_IPSEC_IKE)
	    {
		purposeVector.add(KeyPurposeId.id_kp_ipsecIKE);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_IPSEC_TUNNEL)
	    {
		purposeVector.add(KeyPurposeId.id_kp_ipsecTunnel);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_IPSEC_USER)
	    {
		purposeVector.add(KeyPurposeId.id_kp_ipsecUser);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_SBGPCERTAA_SERVERAUTH)
	    {
		purposeVector.add(KeyPurposeId.id_kp_sbgpCertAAServerAuth);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_SCVP_RESPONDER)
	    {
		purposeVector.add(KeyPurposeId.id_kp_scvp_responder);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_SCVP_CLIENT)
	    {
		purposeVector.add(KeyPurposeId.id_kp_scvpClient);
	    }
	    else if (extendedKeyUsageValue.getExtendedKeyUsage() == ExtendedKeyUsageType.ID_KP_SCVP_SERVER)
	    {
		purposeVector.add(KeyPurposeId.id_kp_scvpServer);
	    }
	}

	ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(purposeVector);
	certificateGenerator.addExtension(X509Extension.extendedKeyUsage, certificateField.getCritical().booleanValue(), extendedKeyUsage);

	return (certificateGenerator);
    }

}
