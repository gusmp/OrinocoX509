package org.orinocoX509.repository.impl.jpa.custom.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.repository.CertificateStatusRepository;
import org.orinocoX509.repository.impl.jpa.custom.CertificateStatusRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CertificateStatusRepositoryImpl implements CertificateStatusRepositoryCustom
{
    @Autowired
    private CertificateStatusRepository certificateStatusRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(CertificateStatusRepositoryImpl.class);

    @Override
    public CertificateStatus saveStatus(CertificateStatus certificateStatus)
    {
	certificateStatus.setLastUpdate(new Date());
	if (certificateStatus.getCertificateStatusId() == null)
	{
	    log.debug("Save status: " + certificateStatus.getCertificateStatus() + " for certificate " + certificateStatus.getCertificateSerialNumber());
	    em.persist(certificateStatus);
	}
	else
	{
	    log.debug("Update status: " + certificateStatus.getCertificateStatus() + " for certificate " + certificateStatus.getCertificateSerialNumber());
	    em.merge(certificateStatus);
	}

	return (certificateStatus);
    }

    @Override
    public CertificateStatus getStatus(CertificateStatus certificateStatus)
    {
	// search by serial number
	try
	{
	    log.debug("Search status for certificate: " + certificateStatus.getCertificateSerialNumber());
	    CertificateStatus certStatus = certificateStatusRepository.findByCertificateSerialNumber(certificateStatus.getCertificateSerialNumber());
	    if (certStatus == null)
	    {
		throw new NoResultException();
	    }

	    return certStatus;
	}
	catch (NoResultException exc)
	{
	    log.debug("Search status for certificate: " + certificateStatus.getCertificateSerialNumber() + " No result");
	    certificateStatus.setCertificateStatus(CertificateStatusValues.U);
	    return (certificateStatus);
	}
    }

}
