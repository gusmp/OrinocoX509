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
@Table(name = "OX509_CER_CERTIFICATE_POLICY_FIELD_VALUE")
@DiscriminatorValue(value = DiscriminatorValues.CERTIFICATE_POLICY)
@Getter
@Setter
public class CertificatePolicyFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = -4149338196448619464L;

    public static enum CertificatePolicyType
    {
	CPS, USER_NOTICE;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "CERTIFICATE_POLICY_TYPE")
    private CertificatePolicyType certificatePolicyType;

    @Column(name = "CERTIFICATE_POLICY")
    private String certificatePolicy;

    public CertificatePolicyFieldValue()
    {
    }

    public CertificatePolicyFieldValue(CertificatePolicyType certificatePolicyType, String certificatePolicy)
    {
	this.certificatePolicyType = certificatePolicyType;
	this.certificatePolicy = certificatePolicy;
    }
}
