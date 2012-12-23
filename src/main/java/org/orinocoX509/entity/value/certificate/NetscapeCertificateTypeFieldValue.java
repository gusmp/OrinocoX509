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
@Table(name = "CER_NETSCAPE_CERTIFICATE_TYPE_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.NETSCAPE_CERTIFICATE_TYPE)
public class NetscapeCertificateTypeFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = 8682185504633915595L;

    public static enum NetscapeCertificateTypeType
    {
	OBJECT_SIGNING, OBJECT_SIGNING_CA, SMIME, SMIME_CA, SSL_CA, SSL_CLIENT, SSL_SERVER, RESERVED;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "NETSCAPE_CERTIFICATE_TYPE")
    private NetscapeCertificateTypeType netscapeCertificateType;

    public NetscapeCertificateTypeFieldValue()
    {
    }

    public NetscapeCertificateTypeFieldValue(NetscapeCertificateTypeType netscapeCertificateType)
    {
	this.netscapeCertificateType = netscapeCertificateType;
    }
}
