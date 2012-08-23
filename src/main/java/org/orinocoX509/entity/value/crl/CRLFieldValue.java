package org.orinocoX509.entity.value.crl;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import org.orinocoX509.entity.field.crl.CRLField;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CRL_CRL_FIELD_VALUE")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="CRL_FIELD_VALUE_TYPE",discriminatorType=DiscriminatorType.INTEGER)
@Getter @Setter
public abstract class CRLFieldValue implements Serializable
{
	private static final long serialVersionUID = -3235483397990764642L;

	@TableGenerator(name="CRLFieldValueId", 
			table="GENERATOR_TABLE", 
			pkColumnName="SEQUENCE_NAME", 
			valueColumnName="SEQUENCE_VALUE")
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE,generator="CRLFieldValueId")
	@Column(name="CRL_FIELD_VALUE_ID")
	private Long crlFieldValueId;
	
	@ManyToOne
	@JoinColumn(name="CRL_FIELD_ID")
	CRLField crlField;

}