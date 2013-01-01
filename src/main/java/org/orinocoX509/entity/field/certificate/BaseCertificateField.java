package org.orinocoX509.entity.field.certificate;

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
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_CERTIFICATE_FIELD")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CERTIFICATE_FIELD_TYPE", discriminatorType = DiscriminatorType.INTEGER)
@Getter
@Setter
public abstract class BaseCertificateField implements Serializable
{
    private static final long serialVersionUID = -3638792618224867031L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CertificateFieldIdGenerator")
    @SequenceGenerator(name = "CertificateFieldIdGenerator", sequenceName = "CERTIFICATE_FIELD_ID_SEQUENCE")
    @Column(name = "CERTIFICATE_FIELD_ID")
    private Long certificateFieldId;

    @ManyToOne
    @JoinColumn(name = "PROFILE_ID")
    protected CertificateProfile certificateProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "FIELD_TYPE")
    protected FieldType fieldType;

    @Column(name = "CRITICAL")
    protected Boolean critical;

    @OneToMany(targetEntity = BaseCertificateFieldValue.class, mappedBy = "certificateField", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BaseCertificateFieldValue> values;

    public List<BaseCertificateFieldValue> addValue(BaseCertificateFieldValue value)
    {
	value.setCertificateField(this);
	this.values.add(value);
	return (this.values);
    }

    public BaseCertificateField()
    {
	this.values = new ArrayList<BaseCertificateFieldValue>(10);
    }

    public BaseCertificateField(CertificateProfile certificateProfile, FieldType fieldType, Boolean critical)
    {
	this();
	this.certificateProfile = certificateProfile;
	this.fieldType = fieldType;
	this.critical = critical;
    }
}
