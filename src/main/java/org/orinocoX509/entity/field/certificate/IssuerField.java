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
@Table(name="CER_ISSUER_FIELD")
@DiscriminatorValue(value=DiscriminatorValues.ISSUER)
@Getter @Setter
public class IssuerField extends CertificateField 
{

	private static final long serialVersionUID = 7906992609984693236L;
	
	@Column(name="PATTERN")
	private String pattern;
	
	public IssuerField() {}
	
	public IssuerField(CertificateProfile certificateProfile, String pattern) 
	{
		this.certificateProfile = certificateProfile;
		this.fieldType = FieldType.ISSUER;
		this.critical = true;
		this.pattern = pattern;
	}
}
