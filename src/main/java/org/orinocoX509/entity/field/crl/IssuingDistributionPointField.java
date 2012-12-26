package org.orinocoX509.entity.field.crl;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.consts.CRLDiscriminatorValues;

@Entity
@Table(name = "CRL_ISSUING_DISTRIBUTION_POINT_FIELD")
@DiscriminatorValue(value = CRLDiscriminatorValues.ISSUING_DISTRIBUTION_POINT)
public class IssuingDistributionPointField extends BaseCRLField implements Serializable
{
    private static final long serialVersionUID = -3359034860044470735L;

    public IssuingDistributionPointField()
    {
    }

    public IssuingDistributionPointField(CRLProfile crlProfile, Boolean critical)
    {
	super(crlProfile, CRLFieldType.ISSUING_DISTRIBUTION_POINT, critical);
    }
}
