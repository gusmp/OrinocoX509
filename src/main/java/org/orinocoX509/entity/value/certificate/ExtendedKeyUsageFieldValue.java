package org.orinocoX509.entity.value.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.orinocoX509.entity.consts.DiscriminatorValues;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_EXTENDED_KEY_USAGE_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.EXTENDED_KEY_USAGE)
public class ExtendedKeyUsageFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = -499440804546067662L;

    public static enum ExtendedKeyUsageType
    {
	ID_KP_SERVER_AUTH, ID_KP_CLIENT_AUTH, ID_KP_CODE_SIGNING, ID_KP_EMAIL_PROTECTION, ID_KP_TIME_STAMPING, ID_KP_OCSP_SIGNING, ID_KP_SMARTCARD_LOGON, ID_KP_CAP_WAP_AC, ID_KP_CAPWAPWTP, ID_KP_DVCS, ID_KP_EAP_OVER_LAN, ID_KP_EAP_OVER_PPP, ID_KP_IPSEC_ENDSYSTEM, ID_KP_IPSEC_IKE, ID_KP_IPSEC_TUNNEL, ID_KP_IPSEC_USER, ID_KP_SBGPCERTAA_SERVERAUTH, ID_KP_SCVP_RESPONDER, ID_KP_SCVP_CLIENT, ID_KP_SCVP_SERVER;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "EXTENDED_KEY_USAGE")
    private ExtendedKeyUsageType extendedKeyUsage;

    public ExtendedKeyUsageFieldValue()
    {
    }

    public ExtendedKeyUsageFieldValue(ExtendedKeyUsageType extendedKeyUsage)
    {
	this.extendedKeyUsage = extendedKeyUsage;
    }
}
