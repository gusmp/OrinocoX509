package org.orinocoX509.entity.field.crl;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.consts.CRLDiscriminatorValues;

@Entity
@Table(name = "CRL_CRL_NUMBER_FIELD")
@DiscriminatorValue(value = CRLDiscriminatorValues.CRL_NUMBER)
@Getter
@Setter
public class CRLNumberField extends BaseCRLField implements Serializable
{
    private static final long serialVersionUID = 3869739936169216320L;

    @Column(name = "CRL_NUMBER")
    private BigInteger crlNumber;

    public CRLNumberField()
    {
    }

    public CRLNumberField(CRLProfile crlProfile, Boolean critical)
    {
	super(crlProfile, CRLFieldType.CRL_NUMBER, critical);
	this.crlNumber = BigInteger.ONE;
    }
}
