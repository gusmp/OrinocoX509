package org.orinocoX509.entity.field.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="CER_CERTIFICATE_POLICY_FIELD")
@DiscriminatorValue(value=DiscriminatorValues.CERTIFICATE_POLICY)
@Getter @Setter
public class CertificatePolicyField extends CertificateField 
{
	private static final long serialVersionUID = 4086830377044391667L;
	
	@Column(name="POLICY_OID")
	String policyOid;
 
	public CertificatePolicyField()  {}
	
	public CertificatePolicyField(CertificateProfile certificateProfile, String policyOid, Boolean critical)  
	{
		this.certificateProfile = certificateProfile;
		this.fieldType = FieldType.CERTIFICATE_POLICY;
		this.critical = critical;
		this.policyOid = policyOid;
	}
}
