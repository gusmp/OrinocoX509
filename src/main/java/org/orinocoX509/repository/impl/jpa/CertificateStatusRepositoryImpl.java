package org.orinocoX509.repository.impl.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.repository.CertificateStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CertificateStatusRepositoryImpl implements CertificateStatusRepository
{
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
    public void deleteStatus(CertificateStatus certificateStatus)
    {
	certificateStatus = em.merge(certificateStatus);
	log.debug("Delete status for certificate: " + certificateStatus.getCertificateSerialNumber());
	em.remove(certificateStatus);
    }

    @Override
    public CertificateStatus getStatus(CertificateStatus certificateStatus)
    {
	// search by serial number
	try
	{
	    log.debug("Search status for certificate: " + certificateStatus.getCertificateSerialNumber());
	    TypedQuery<CertificateStatus> q = em.createQuery("SELECT cs FROM CertificateStatus cs WHERE cs.certificateSerialNumber= :certificateSerialNumber", CertificateStatus.class);
	    q.setParameter("certificateSerialNumber", certificateStatus.getCertificateSerialNumber());
	    log.debug("Search status for certificate: " + certificateStatus.getCertificateSerialNumber() + " OK");
	    return q.getSingleResult();
	}
	catch (NoResultException exc)
	{
	    log.debug("Search status for certificate: " + certificateStatus.getCertificateSerialNumber() + " No result");
	    certificateStatus.setCertificateStatus(CertificateStatusValues.U);
	    return (certificateStatus);
	}
    }

    @Override
    public List<CertificateStatus> getCertificatesStatus(CertificateStatusValues certificateStatusValue)
    {
	log.debug("Search for certificates with status: " + certificateStatusValue);
	TypedQuery<CertificateStatus> q = em.createQuery("SELECT cs FROM CertificateStatus cs WHERE cs.certificateStatus= :certificateStatusValue", CertificateStatus.class);
	q.setParameter("certificateStatusValue", certificateStatusValue);
	log.debug("Search for certificates with status: " + certificateStatusValue + " OK. Results: " + q.getResultList().size());
	return q.getResultList();
    }

}
