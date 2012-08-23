package org.orinocoX509.service.impl;

import java.util.Date;
import java.util.List;

import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.repository.CertificateStatusRepository;
import org.orinocoX509.service.CertificateStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificateStatusServiceImpl implements CertificateStatusService 
{
	@Autowired
	CertificateStatusRepository certificateStatusRepository;
	
	private static final Logger log = LoggerFactory.getLogger(CertificateStatusServiceImpl.class);


	@CacheEvict(value="crlCache", allEntries=true)
	@Transactional(readOnly = false)
	public CertificateStatus saveStatus(CertificateStatus certificateStatus)
	{
		log.debug("Save status: " + certificateStatus.getCertificateStatus() + " for certificate " + certificateStatus.getCertificateSerialNumber() );
		return(certificateStatusRepository.saveStatus(certificateStatus));
	}


	@CacheEvict(value="crlCache", allEntries=true)
	@Transactional(readOnly = false)
	public void deleteStatus(CertificateStatus certificateStatus) 
	{
		log.debug("Delete status for certificate: " + certificateStatus.getCertificateSerialNumber());
		certificateStatusRepository.deleteStatus(certificateStatus);
	}

	@Transactional(readOnly = true)
	public CertificateStatus getStatus(CertificateStatus certificateStatus) 
	{
		log.debug("Search status for certificate: " +  certificateStatus.getCertificateSerialNumber());
		certificateStatus = certificateStatusRepository.getStatus(certificateStatus);
		
		if (certificateStatus.getCertificateStatus() != CertificateStatusValues.U)
		{
			if (certificateStatus.getNotAfter().before(new Date()) == true)
			{
				certificateStatus.setCertificateStatus(CertificateStatusValues.E);
			}
		}
		
		return(certificateStatus);
	}
	
	@Transactional(readOnly = true)
	public List<CertificateStatus> getCertificatesStatus(CertificateStatusValues certificateStatusValue) 
	{
		log.debug("Search certificates with status: " +  certificateStatusValue);
		return(certificateStatusRepository.getCertificatesStatus(certificateStatusValue));
	}
}
