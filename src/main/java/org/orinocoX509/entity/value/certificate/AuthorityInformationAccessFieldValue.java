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
@Table(name = "OX509_CER_AUTHORITY_INFORMATION_ACCESS_FIELD_VALUE")
@DiscriminatorValue(value = DiscriminatorValues.AUTHORITY_INFORMATION_ACCESS)
@Getter
@Setter
public class AuthorityInformationAccessFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = -2458891505957302915L;

    public static enum AIAType
    {
	ID_AD_OCSP, ID_AD_CA_ISSUERS;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "AIA_TYPE")
    private AIAType aiaType;

    @Column(name = "AIA_VALUE")
    private String aiaValue;

    public AuthorityInformationAccessFieldValue()
    {
    }

    public AuthorityInformationAccessFieldValue(AIAType aiaType, String aiaValue)
    {
	this.aiaType = aiaType;
	this.aiaValue = aiaValue;
    }
}
