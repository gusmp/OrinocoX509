package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "OX509_CER_AUTHORITY_KEY_IDENTIFIER")
@DiscriminatorValue(value = DiscriminatorValues.AUTHORITY_KEY_IDENTIFIER)
public class AuthorityKeyIdentifierField extends BaseCertificateField
{
    private static final long serialVersionUID = 4621499598570988961L;

    public AuthorityKeyIdentifierField()
    {
    }

    public AuthorityKeyIdentifierField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.AUTHORITY_KEY_IDENTIFIER, critical);
    }
}
