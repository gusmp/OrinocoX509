package org.orinocoX509.entity.value.certificate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter @Setter
public abstract class KeyValueFieldValue extends CertificateFieldValue 
{
	private static final long serialVersionUID = 2390612123367024512L;

	@Column(name="PATTERN_KEY")
	protected String patternKey;
	
	@Column(name="PATTERN_VALUE")
	protected String patternValue;
 
	public KeyValueFieldValue() {}
	
	public KeyValueFieldValue(String patternKey, String patternValue) 
	{
		this.patternKey = patternKey;
		this.patternValue = patternValue;
	}
}
