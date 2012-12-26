package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_CRL_DISTRIBUTION_POINT_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.CRL_DISTRIBUTION_POINT)
public class CRLDistributionPointField extends BaseCertificateField
{
    private static final long serialVersionUID = -7521726666640799210L;

    public CRLDistributionPointField()
    {
    }

    public CRLDistributionPointField(CertificateProfile certificateProfile, Boolean critical)
    {
	super(certificateProfile, FieldType.CRL_DISTRIBUTION_POINT,critical);
    }
}
