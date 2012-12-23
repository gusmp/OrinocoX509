package org.orinocoX509.entity.value.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_ISSUER_FIELD_VALUE")
@DiscriminatorValue(value = DiscriminatorValues.ISSUER)
public class IssuerFieldValue extends KeyValueFieldValue
{
    private static final long serialVersionUID = 1689833160294451384L;

    public IssuerFieldValue()
    {
    }

    public IssuerFieldValue(String patternKey, String patternValue)
    {
	this.patternKey = patternKey;
	this.patternValue = patternValue;
    }
}
