package org.orinocoX509.entity.field.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_DEVICE_TYPE_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.DEVICE_TYPE)
@Getter
@Setter
public class DeviceTypeField extends BaseCertificateField
{

    private static final long serialVersionUID = 952236551092152163L;

    @Column(name = "DEVICE_TYPE")
    private String deviceType;

    public DeviceTypeField()
    {
    }

    public DeviceTypeField(CertificateProfile certificateProfile, String deviceType, Boolean critical)
    {
	super(certificateProfile, FieldType.DEVICE_TYPE, critical);
	this.deviceType = deviceType;
    }
}
