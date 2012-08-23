package org.orinocoX509.entity.field.crl;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.consts.CRLDiscriminatorValues;


@Entity
@Table(name="CRL_AUTHORITY_KEY_IDENTIFIER_FIELD")
@DiscriminatorValue(value=CRLDiscriminatorValues.AUTHORITY_KEY_IDENTIFIER)
@Getter @Setter
public class AuthorityKeyIdentifierCRLField extends CRLField implements Serializable
{
	private static final long serialVersionUID = -5875850025792549258L;

	@Column(name="AUTH_KEY_IDENTIFIER")
	private Boolean authorityKeyIdentifier;
	
	@Column(name="AUTH_ISSUER_SERIAL_NUMBER")
	private Boolean authorityIssuerSerialNumberCertificate;
	
	public AuthorityKeyIdentifierCRLField() {}
	
	public AuthorityKeyIdentifierCRLField(CRLProfile crlProfile, Boolean critical, Boolean authorityKeyIdentifier, Boolean authorityIssuerSerialNumberCertificate)
	{
		this.crlFieldType = CRLFieldType.AUTHORITY_KEY_IDENTIFIER;
		this.crlProfile = crlProfile;
		this.critical = critical;
		this.authorityKeyIdentifier = authorityKeyIdentifier;
		this.authorityIssuerSerialNumberCertificate = authorityIssuerSerialNumberCertificate;
	}
}
