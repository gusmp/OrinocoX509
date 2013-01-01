package org.orinocoX509.entity.value.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.orinocoX509.entity.consts.DiscriminatorValues;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_AUTHORITY_KEY_IDENTIFIER_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.AUTHORITY_KEY_IDENTIFIER)
public class AuthorityKeyIdentifierFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = 8487281382222695983L;

    public static enum AuthorityKeyIdentifierType
    {
	KEY_IDENTIFIER, AUTH_CERT_ISSUER_AUTH_CERT_SERIAL_NUMBER;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTHORITY_KEY_IDENTIFIER")
    private AuthorityKeyIdentifierType authorityKeyIdentifier;

    public AuthorityKeyIdentifierFieldValue()
    {
    }

    public AuthorityKeyIdentifierFieldValue(AuthorityKeyIdentifierType authorityKeyIdentifier)
    {
	this.authorityKeyIdentifier = authorityKeyIdentifier;
    }
}
