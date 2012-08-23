package org.orinocoX509.entity.field.certificate;

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
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.value.certificate.CertificateFieldValue;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CER_CERTIFICATE_FIELD")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="CERTIFICATE_FIELD_TYPE",discriminatorType = DiscriminatorType.INTEGER)
@Getter @Setter
public abstract class CertificateField implements Serializable
{
	private static final long serialVersionUID = -3638792618224867031L;

	@TableGenerator(name="CertificateFieldId", 
			table="GENERATOR_TABLE", 
			pkColumnName="SEQUENCE_NAME", 
			valueColumnName="SEQUENCE_VALUE")
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE,generator="CertificateFieldId")
	@Column(name="CERTIFICATE_FIELD_ID")
	private Long certificateFieldId;
	
	@ManyToOne
	@JoinColumn(name="PROFILE_ID")
	protected CertificateProfile certificateProfile;
	
	@Enumerated(EnumType.STRING)
	@Column(name="FIELD_TYPE")
	protected FieldType fieldType;
	
	@Column(name="CRITICAL")
	protected Boolean critical;
	
	@OneToMany(targetEntity=CertificateFieldValue.class, mappedBy="certificateField", orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<CertificateFieldValue> values;
	
	public List<CertificateFieldValue> addValue(CertificateFieldValue value)
	{
		value.setCertificateField(this);
		this.values.add(value);
		return(this.values);
	}
	
	public CertificateField() 
	{ 
		this.values = new ArrayList<CertificateFieldValue>(10);
	}
}
