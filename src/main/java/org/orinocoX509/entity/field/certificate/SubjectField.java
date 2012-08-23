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
@Table(name="CER_SUBJECT_FIELD")
@DiscriminatorValue(value=DiscriminatorValues.SUBJECT)
@Getter @Setter
public class SubjectField extends CertificateField 
{
	private static final long serialVersionUID = -5141806943290855915L;
	
	@Column(name="PATTERN")
	private String pattern;
	
	public SubjectField() {}
	
	public SubjectField(CertificateProfile certificateProfile, String pattern) 
	{
		this.certificateProfile = certificateProfile;
		this.fieldType = FieldType.SUBJECT;
		this.critical = true;
		this.pattern = pattern;
	}
}
