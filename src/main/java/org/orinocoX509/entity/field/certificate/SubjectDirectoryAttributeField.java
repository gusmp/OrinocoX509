package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_SUBJECT_DIRECTORY_ATTRIBUTE_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT_DIRECTORY_ATTRIBUTE)
@Getter
@Setter
public class SubjectDirectoryAttributeField extends BaseCertificateField
{
    private static final long serialVersionUID = -5141806943290855915L;

    public SubjectDirectoryAttributeField()
    {
    }

    public SubjectDirectoryAttributeField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.SUBJECT_DIRECTORY_ATTRIBUTE, critical);

    }
}
