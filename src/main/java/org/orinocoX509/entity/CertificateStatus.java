package org.orinocoX509.entity;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CER_CERTIFICATE_STATUS")
@Getter
@Setter
@ToString
public class CertificateStatus
{
    public static enum CertificateStatusValues
    {
	V, // 0
	S, // 1
	R, // 2
	U, // 3
	E, // 4
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CertificateStatusIdGenerator")
    @SequenceGenerator(name = "CertificateStatusIdGenerator", sequenceName = "CERTIFICATE_STATUS_ID_SEQUENCE")
    @Column(name = "CERTIFICATE_STATUS_ID")
    private Integer certificateStatusId;

    @Column(name = "CERTIFICATE_SERIAL_NUMBER", unique=true)
    private BigInteger certificateSerialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private CertificateStatusValues certificateStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NOT_BEFORE")
    private Date notBefore;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NOT_AFTER")
    private Date notAfter;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATE")
    private Date lastUpdate;

    public CertificateStatus()
    {
    }

    public CertificateStatus(BigInteger certificateSerialNumber)
    {
	this(certificateSerialNumber, null, null, null);
    }

    public CertificateStatus(BigInteger certificateSerialNumber, CertificateStatusValues certificateStatus, Date notAfter, Date notBefore)
    {
	this.certificateSerialNumber = certificateSerialNumber;
	this.certificateStatus = certificateStatus;
	this.notAfter = notAfter;
	this.notBefore = notBefore;
    }

}
