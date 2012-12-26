package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_KEY_USAGE_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.KEY_USAGE)
public class KeyUsageField extends BaseCertificateField
{

    private static final long serialVersionUID = 4169501820865199294L;

    public KeyUsageField()
    {
    }

    public KeyUsageField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.KEY_USAGE, critical);
    }
}
