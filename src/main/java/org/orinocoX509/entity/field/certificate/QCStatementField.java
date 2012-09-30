package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;


@Entity
@Table(name="CER_QUALIFIED_CERTIFICATE_STATEMENT_FIELD")
@DiscriminatorValue(value=DiscriminatorValues.QUALIFIED_CERTIFICATE_STATEMENT)
public class QCStatementField extends CertificateField 
{
	private static final long serialVersionUID = -5212459197833239198L;

	public QCStatementField()  { }
	
	public QCStatementField(CertificateProfile certificateProfile, Boolean critical) 
	{
		this.certificateProfile = certificateProfile;
		this.fieldType = FieldType.QUALIFIED_CERTIFICATE_STATEMENT;
		this.critical = critical;
	}
}
