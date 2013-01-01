package org.orinocoX509.entity.value.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.orinocoX509.entity.consts.DiscriminatorValues;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_SUBJECT_ALTERNATIVE_NAME_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT_ALTERNATIVE_NAME)
public class SubjectAlternativeNameFieldValue extends AlternativeNameFieldValue
{
    private static final long serialVersionUID = -1027487556168601462L;

    public SubjectAlternativeNameFieldValue()
    {
    }

    public SubjectAlternativeNameFieldValue(AlternativeNameType alternativeNameType)
    {
	this.alternativeNameType = alternativeNameType;
	this.value = null;
    }

    public SubjectAlternativeNameFieldValue(AlternativeNameType alternativeNameType, String value)
    {
	this.alternativeNameType = alternativeNameType;
	this.value = value;
    }
}
