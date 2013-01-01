package org.orinocoX509.entity.value.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "OX509_CER_SUBJECT_FIELD_VALUE")
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT)
public class SubjectFieldValue extends KeyValueFieldValue
{
    private static final long serialVersionUID = 8804154667372973248L;

    public SubjectFieldValue()
    {
    }

    public SubjectFieldValue(String patternKey, String patternValue)
    {
	this.patternKey = patternKey;
	this.patternValue = patternValue;
    }
}
