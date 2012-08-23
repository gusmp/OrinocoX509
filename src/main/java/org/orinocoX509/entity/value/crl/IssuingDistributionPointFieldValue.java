package org.orinocoX509.entity.value.crl;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.orinocoX509.entity.consts.CRLDiscriminatorValues;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="CRL_ISSUING_DISTRIBUTION_POINT_FIELD_VALUE")
@Getter @Setter
@DiscriminatorValue(value=CRLDiscriminatorValues.ISSUING_DISTRIBUTION_POINT)
public class IssuingDistributionPointFieldValue extends CRLFieldValue implements Serializable 
{
	private static final long serialVersionUID = -9097734349936773536L;

	@Column(name="ONLY_CONTAINS_USER_CERTS")
	private boolean onlyContainsUserCerts;
	
	@Column(name="ONLY_CONTAINS_CA_CERTS")
	private boolean onlyContainsCACerts; 
	
	@ElementCollection
	@CollectionTable(name="CRL_ISSUING_DISTRIBUTION_POINT_FIELDS_VALUE_URLS", joinColumns=@JoinColumn(name="CRL_FIELD_VALUE_ID"))
	@Column(name="CRL_URL")
	private List<String> values;
	
	public IssuingDistributionPointFieldValue() {}
	
	public IssuingDistributionPointFieldValue(List<String> values, Boolean onlyContainsUserCerts, Boolean onlyContainsCACerts) 
	{
		this.values = values;
		this.onlyContainsUserCerts = onlyContainsUserCerts;
		this.onlyContainsCACerts = onlyContainsCACerts;
	}
}
