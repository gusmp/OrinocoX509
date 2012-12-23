package org.orinocoX509.entity.field.certificate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.consts.DiscriminatorValues;

@Entity
@Table(name = "CER_NETSCAPE_CERTIFICATE_TYPE_FIELD")
@DiscriminatorValue(value = DiscriminatorValues.NETSCAPE_CERTIFICATE_TYPE)
public class NetscapeCertificateTypeField extends BaseCertificateField
{

    private static final long serialVersionUID = -4760822693384180547L;

    public NetscapeCertificateTypeField()
    {
    }

    public NetscapeCertificateTypeField(CertificateProfile certificateProfile, Boolean critical)
    {
	this.certificateProfile = certificateProfile;
	this.fieldType = FieldType.NETSCAPE_CERTIFICATE_TYPE;
	this.critical = critical;
    }
}
