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
@Table(name = "CER_KEY_USAGE_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.KEY_USAGE)
public class KeyUsageFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = -8768575731923499769L;

    public static enum KeyUsageType
    {
	DIGITAL_SIGNATURE, NON_REPUTATION, KEY_ENCIPHERMENT, DATA_ENCIPHERMENT, KEY_AGREEMENT, KEY_CERT_SIGN, CRL_SIGN, ENCIPHER_ONLY, DECIPHER_ONLY;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "KEY_USAGE")
    private KeyUsageType keyUsage;

    public KeyUsageFieldValue()
    {
    }

    public KeyUsageFieldValue(KeyUsageType keyUsage)
    {
	this.keyUsage = keyUsage;
    }
}
