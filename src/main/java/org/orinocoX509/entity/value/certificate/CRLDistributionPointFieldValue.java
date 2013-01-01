package org.orinocoX509.entity.value.certificate;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.orinocoX509.entity.consts.DiscriminatorValues;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_CRL_DISTRIBUTION_POINT_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.CRL_DISTRIBUTION_POINT)
public class CRLDistributionPointFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = 4895122831481352494L;

    @ElementCollection
    @CollectionTable(name = "OX509_CER_CRL_DISTRIBUTION_POINT_FIELDS_VALUE_URLS", joinColumns = @JoinColumn(name = "CERTIFICATE_FIELD_VALUE_ID"))
    @Column(name = "CRL_URL")
    private List<String> values;

    public CRLDistributionPointFieldValue()
    {
    }

    public CRLDistributionPointFieldValue(List<String> values)
    {
	this.values = values;
    }
}
