package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_QUALIFIED_CERTIFICATE_STATEMENT_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.QUALIFIED_CERTIFICATE_STATEMENT)
public class QCStatementField extends BaseCertificateField
{
    private static final long serialVersionUID = -5212459197833239198L;

    public QCStatementField()
    {
    }

    public QCStatementField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.QUALIFIED_CERTIFICATE_STATEMENT, critical);
    }
}
