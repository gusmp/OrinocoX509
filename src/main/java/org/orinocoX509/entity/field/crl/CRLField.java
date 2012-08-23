package org.orinocoX509.entity.field.crl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.value.crl.CRLFieldValue;

@Entity
@Table(name="CRL_CRL_FIELD")

@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="CRL_FIELD_TYPE",discriminatorType = DiscriminatorType.INTEGER)
@Getter @Setter
public abstract class CRLField implements Serializable
{
	private static final long serialVersionUID = 1136609815691403126L;

	@TableGenerator(name="CRLFieldId", 
			table="GENERATOR_TABLE", 
			pkColumnName="SEQUENCE_NAME", 
			valueColumnName="SEQUENCE_VALUE")
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE,generator="CRLFieldId")
	@Column(name="CRL_FIELD_ID")
	private Long crlFieldId;
	
	@ManyToOne
	@JoinColumn(name="PROFILE_ID")
	protected CRLProfile crlProfile;
	
	@Enumerated(EnumType.STRING)
	@Column(name="FIELD_TYPE")
	protected CRLFieldType crlFieldType;
	
	@Column(name="CRITICAL")
	protected Boolean critical;
	
	@OneToMany(targetEntity=CRLFieldValue.class, mappedBy="crlField", orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<CRLFieldValue> values;
	
	public List<CRLFieldValue> addValue(CRLFieldValue value)
	{
		value.setCrlField(this);
		this.values.add(value);
		return(this.values);
	}
	
	public CRLField()  
	{ 
		this.values = new ArrayList<CRLFieldValue>(1);
	}
	
}
