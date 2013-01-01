package org.orinocoX509.entity.field.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "OX509_CER_BASIC_CONSTRAINT_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.BASIC_CONSTRAINT)
@Getter
@Setter
public class BasicConstraintField extends BaseCertificateField
{
    private static final long serialVersionUID = 9004761888009061829L;

    @Column(name = "IS_CA")
    private Boolean isCA;

    @Column(name = "PATH_LENGTH")
    private Integer pathLength;

    public BasicConstraintField()
    {
    }

    public BasicConstraintField(CertificateProfile certificateProfile, Boolean isCA, Integer pathLength, Boolean critical)
    {
	super(certificateProfile, FieldType.BASIC_CONSTRAINT, critical);
	this.isCA = isCA;
	this.pathLength = pathLength;
    }
}
