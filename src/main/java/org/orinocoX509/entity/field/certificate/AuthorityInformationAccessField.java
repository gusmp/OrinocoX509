package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "OX509_CER_AUTHORITY_INFORMATION_ACCESS_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.AUTHORITY_INFORMATION_ACCESS)
public class AuthorityInformationAccessField extends BaseCertificateField
{
    private static final long serialVersionUID = 8916164722880126586L;

    public AuthorityInformationAccessField()
    {
    }

    public AuthorityInformationAccessField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.AUTHORITY_INFORMATION_ACCESS, critical);
    }
}
