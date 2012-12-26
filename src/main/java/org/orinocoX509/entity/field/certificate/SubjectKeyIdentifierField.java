package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_SUBJECT_KEY_IDENTIFIER_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT_KEY_IDENTIFIER)
public class SubjectKeyIdentifierField extends BaseCertificateField
{
    private static final long serialVersionUID = 3908893062244508316L;

    public SubjectKeyIdentifierField()
    {
    }

    public SubjectKeyIdentifierField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.SUBJECT_KEY_IDENTIFIER, critical);
    }
}
