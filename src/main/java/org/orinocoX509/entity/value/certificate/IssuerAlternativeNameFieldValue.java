package org.orinocoX509.entity.value.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.orinocoX509.entity.consts.DiscriminatorValues;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="CER_ISSUER_ALTERNATIVE_NAME_FIELD_VALUE")
@Getter @Setter
@DiscriminatorValue(value=DiscriminatorValues.ISSUER_ALTERNATIVE_NAME)
public class IssuerAlternativeNameFieldValue extends AlternativeNameFieldValue 
{
	private static final long serialVersionUID = -1254475683790875114L;

	public IssuerAlternativeNameFieldValue() {}
	
	public IssuerAlternativeNameFieldValue(AlternativeNameType alternativeNameType, String value) 
	{
		this.alternativeNameType = alternativeNameType;
		this.value = value;
	}
}
