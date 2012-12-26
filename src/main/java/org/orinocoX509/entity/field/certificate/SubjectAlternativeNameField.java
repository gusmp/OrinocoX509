package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_SUBJECT_ALTERNATIVE_NAME_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT_ALTERNATIVE_NAME)
public class SubjectAlternativeNameField extends BaseCertificateField
{
    private static final long serialVersionUID = -1766533862106004100L;

    public SubjectAlternativeNameField()
    {
    }

    public SubjectAlternativeNameField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.SUBJECT_ALTERNATIVE_NAME, critical);
    }
}
