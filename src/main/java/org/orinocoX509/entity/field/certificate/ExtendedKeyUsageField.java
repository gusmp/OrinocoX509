package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;


@Entity
@Table(name="CER_EXTENDED_KEY_USAGE_FIELD")
@DiscriminatorValue(value=DiscriminatorValues.EXTENDED_KEY_USAGE)
public class ExtendedKeyUsageField extends CertificateField 
{
 	
	private static final long serialVersionUID = -1507831947185812005L;

	public ExtendedKeyUsageField()  { }
	
	public ExtendedKeyUsageField(CertificateProfile certificateProfile, Boolean critical) 
	{
		this.certificateProfile = certificateProfile;
		this.fieldType = FieldType.EXTENDED_KEY_USAGE;
		this.critical = critical;
	}
}
