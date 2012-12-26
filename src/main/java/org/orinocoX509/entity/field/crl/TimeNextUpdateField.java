package org.orinocoX509.entity.field.crl;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.consts.CRLDiscriminatorValues;

@Entity
@Table(name = "CRL_TIME_NEXT_UPDATE_FIELD")
@DiscriminatorValue(value = CRLDiscriminatorValues.TIME_NEXT_UPDATE)
@Getter
@Setter
public class TimeNextUpdateField extends BaseCRLField implements Serializable
{
    private static final long serialVersionUID = -7656094660850572586L;

    private Integer daysNextUpdate;

    public TimeNextUpdateField()
    {
    }

    public TimeNextUpdateField(CRLProfile crlProfile, Integer daysNextUpdate)
    {
	super(crlProfile, CRLFieldType.TIME_NEXT_UPDATE, true);
	this.daysNextUpdate = daysNextUpdate;
    }
}
