package org.orinocoX509.entity.field.crl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.value.crl.BaseCRLFieldValue;

@Entity
@Table(name = "CRL_CRL_FIELD")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CRL_FIELD_TYPE", discriminatorType = DiscriminatorType.INTEGER)
@Getter
@Setter
public abstract class BaseCRLField implements Serializable
{
    private static final long serialVersionUID = 1136609815691403126L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CRLFieldIdGenerator")
    @SequenceGenerator(name = "CRLFieldIdGenerator", sequenceName = "CRL_FIELD_ID_SEQUENCE")
    @Column(name = "CRL_FIELD_ID")
    private Long crlFieldId;

    @ManyToOne
    @JoinColumn(name = "PROFILE_ID")
    protected CRLProfile crlProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "FIELD_TYPE")
    protected CRLFieldType crlFieldType;

    @Column(name = "CRITICAL")
    protected Boolean critical;

    @OneToMany(targetEntity = BaseCRLFieldValue.class, mappedBy = "crlField", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BaseCRLFieldValue> values;

    public List<BaseCRLFieldValue> addValue(BaseCRLFieldValue value)
    {
	value.setCrlField(this);
	this.values.add(value);
	return (this.values);
    }

    public BaseCRLField()
    {
	this.values = new ArrayList<BaseCRLFieldValue>(1);
    }

    public BaseCRLField(CRLProfile crlProfile, CRLFieldType crlFieldType, Boolean critical)
    {
	this();
	this.crlProfile = crlProfile;
	this.crlFieldType = crlFieldType;
	this.critical = critical;
    }

}
