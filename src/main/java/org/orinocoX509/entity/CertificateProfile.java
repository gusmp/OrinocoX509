package org.orinocoX509.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "CER_CERTIFICATE_PROFILE")
@Getter
@Setter
@ToString
public class CertificateProfile implements Serializable
{
    private static final long serialVersionUID = 6575297998086921529L;

    public static enum KeySizeValues
    {
	SIZE_512, // 0
	SIZE_1024, // 1
	SIZE_2048, // 2
	SIZE_4096; // 3

	public int getValue()
	{
	    int value = 0;
	    switch (this.ordinal())
	    {
	    case 0:
		value = 512;
		break;
	    case 1:
		value = 1024;
		break;
	    case 2:
		value = 2048;
		break;
	    case 3:
		value = 4096;
		break;
	    }

	    return (value);
	}
    }

    /*
     * @TableGenerator(name="CertificateProfileId",
     * table="GENERATOR_TABLE",
     * pkColumnName="SEQUENCE_NAME",
     * valueColumnName="SEQUENCE_VALUE")
     * 
     * @Id
     * 
     * @GeneratedValue(strategy=GenerationType.TABLE,generator=
     * "CertificateProfileId")
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CertificateProfileIdGenerator")
    @SequenceGenerator(name = "CertificateProfileIdGenerator", sequenceName = "CERTIFICATE_PROFILE_ID_SEQUENCE")
    @Column(name = "PROFILE_ID")
    private Integer profileId;

    @Column(name = "PROFILE_NAME", length = 75)
    private String profileName;

    @Column(name = "PROFILE_DESCRIPTION", length = 250)
    private String profileDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "KEY_SIZE")
    private KeySizeValues keySize;

    @Column(name = "YEARS")
    private Integer years;

    @Version
    private Long version;

    @OneToMany(targetEntity = BaseCertificateField.class, mappedBy = "certificateProfile", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BaseCertificateField> fields;

    public CertificateProfile()
    {
    }

    public CertificateProfile(String profileName, String profileDescription, Integer duration, KeySizeValues keySize)
    {
	this.profileName = profileName;
	this.profileDescription = profileDescription;
	this.years = duration;
	this.keySize = keySize;
	this.fields = new ArrayList<BaseCertificateField>(10);
    }

    public List<BaseCertificateField> addField(BaseCertificateField field)
    {
	if (getField(field.getFieldType()) != null)
	{
	    throw new EngineException(EngineErrorCodes.DUPLICATE_FIELD_IN_PROFILE, "The field " + field.getFieldType().name() + " already exists in the profile " + this.profileName);
	}

	field.setCertificateProfile(this);
	this.fields.add(field);
	return (this.fields);
    }

    public List<BaseCertificateField> getFields()
    {
	return (this.fields);
    }

    public BaseCertificateField getField(FieldType fieldType)
    {
	for (BaseCertificateField field : this.fields)
	{
	    if (fieldType == field.getFieldType())
	    {
		return (field);
	    }
	}
	return (null);
    }
}
