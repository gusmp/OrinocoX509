package org.orinocoX509.entity.field.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_SUBJECT_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT)
@Getter
@Setter
public class SubjectField extends BaseCertificateField
{
    private static final long serialVersionUID = -5141806943290855915L;

    @Column(name = "PATTERN")
    private String pattern;

    public SubjectField()
    {
    }

    public SubjectField(CertificateProfile certificateProfile, String pattern)
    {
	super(certificateProfile, FieldType.SUBJECT, true);
	this.pattern = pattern;
    }
}
