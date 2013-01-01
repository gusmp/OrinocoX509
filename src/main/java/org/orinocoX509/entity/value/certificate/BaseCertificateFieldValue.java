package org.orinocoX509.entity.value.certificate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orinocoX509.entity.field.certificate.BaseCertificateField;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OX509_CER_CERTIFICATE_FIELD_VALUE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CERTIFICATE_FIELD_VALUE_TYPE", discriminatorType = DiscriminatorType.INTEGER)
@Getter
@Setter
public abstract class BaseCertificateFieldValue implements Serializable
{
    private static final long serialVersionUID = -6959838780836400998L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CertificateFieldValueIdGenerator")
    @SequenceGenerator(name = "CertificateFieldValueIdGenerator", sequenceName = "CERTIFICATE_FIELD_VALUE_ID_SEQUENCE")
    @Column(name = "CERTIFICATE_FIELD_VALUE_ID")
    private Long certificateFieldValueId;

    @ManyToOne
    @JoinColumn(name = "CERTIFICATE_FIELD_ID")
    private BaseCertificateField certificateField;

}
