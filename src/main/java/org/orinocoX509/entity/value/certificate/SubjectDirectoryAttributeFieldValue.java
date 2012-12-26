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
@Table(name = "CER_SUBJECT_DIRECTORY_ATTRIBUTE_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.SUBJECT_DIRECTORY_ATTRIBUTE)
public class SubjectDirectoryAttributeFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = -1027487556168601462L;

    public static enum SubjectDirectoryAttributeType
    {
	COUNTRY_OF_CITIZENSHIP("1.3.6.1.5.5.7.9.4"), COUNTRY_OF_RESIDENCE("1.3.6.1.5.5.7.9.5");

	private final String oid;

	private SubjectDirectoryAttributeType(String pOid)
	{
	    oid = pOid;
	}

	@Override
	public String toString()
	{
	    return oid;
	}
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "SUBJECT_DIRECTORY_ATTRIBUTE")
    private SubjectDirectoryAttributeType subjectDirectoryAttribute;

    @Column(name = "VALUE")
    private String value;

    public SubjectDirectoryAttributeFieldValue()
    {
    }

    public SubjectDirectoryAttributeFieldValue(SubjectDirectoryAttributeType subjectDirectoryAttribute, String value)
    {
	this.subjectDirectoryAttribute = subjectDirectoryAttribute;
	this.value = value;
    }
}
