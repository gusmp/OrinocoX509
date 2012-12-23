package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_ISSUER_ALTERNATIVE_NAME_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.ISSUER_ALTERNATIVE_NAME)
public class IssuerAlternativeNameField extends BaseCertificateField
{

    private static final long serialVersionUID = -8821687877006348349L;

    public IssuerAlternativeNameField()
    {
    }

    public IssuerAlternativeNameField(CertificateProfile certificateProfile, Boolean critical)
    {
	this.certificateProfile = certificateProfile;
	this.fieldType = FieldType.ISSUER_ALTERNATIVE_NAME;
	this.critical = critical;
    }
}
