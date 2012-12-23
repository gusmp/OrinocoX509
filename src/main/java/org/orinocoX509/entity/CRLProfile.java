package org.orinocoX509.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import org.orinocoX509.entity.field.crl.BaseCRLField;
import org.orinocoX509.entity.field.crl.CRLFieldType;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "CRL_CRL_PROFILE")
@Getter
@Setter
@ToString
public class CRLProfile implements Serializable
{
    private static final long serialVersionUID = 8272548946910126160L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CRLProfileIdGenerator")
    @SequenceGenerator(name = "CRLProfileIdGenerator", sequenceName = "CRL_PROFILE_ID_SEQUENCE")
    @Column(name = "PROFILE_ID")
    private Integer profileId;

    @Column(name = "PROFILE_NAME", length = 75)
    private String profileName;

    @Column(name = "PROFILE_DESCRIPTION", length = 250)
    private String profileDescription;

    @Version
    private Long version;

    @OneToMany(targetEntity = BaseCRLField.class, mappedBy = "crlProfile", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BaseCRLField> fields;

    public CRLProfile()
    {
    }

    public CRLProfile(String profileName, String profileDescription)
    {
	this.profileName = profileName;
	this.profileDescription = profileDescription;
	this.fields = new ArrayList<BaseCRLField>(3);
    }

    public List<BaseCRLField> addField(BaseCRLField crlField)
    {
	if (getField(crlField.getCrlFieldType()) != null)
	{
	    throw new EngineException(EngineErrorCodes.DUPLICATE_FIELD_IN_PROFILE, "The field " + crlField.getCrlFieldType().name() + " already exists in the profile " + this.profileName);
	}

	crlField.setCrlProfile(this);
	this.fields.add(crlField);
	return (this.fields);
    }

    public List<BaseCRLField> getFields()
    {
	return (this.fields);
    }

    public BaseCRLField getField(CRLFieldType crlFieldType)
    {
	for (BaseCRLField crlField : this.fields)
	{
	    if (crlFieldType == crlField.getCrlFieldType())
	    {
		return (crlField);
	    }
	}
	return (null);
    }

}
