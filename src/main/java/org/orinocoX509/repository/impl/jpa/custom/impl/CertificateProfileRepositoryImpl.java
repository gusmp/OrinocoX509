package org.orinocoX509.repository.impl.jpa.custom.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.repository.CertificateProfileRepository;
import org.orinocoX509.repository.impl.jpa.custom.CertificateProfileRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CertificateProfileRepositoryImpl implements CertificateProfileRepositoryCustom
{
    @Autowired
    private CertificateProfileRepository certificateProfileRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(CertificateProfileRepositoryImpl.class);

    @Override
    public CertificateProfile saveProfile(CertificateProfile certificateProfile)
    {

	if (certificateProfile.getProfileId() == null)
	{
	    if (getProfile(certificateProfile.getProfileName()) != null)
	    {
		log.debug("The profile " + certificateProfile.toString() + " already exists");
		throw new EngineException(EngineErrorCodes.DUPLICATE_PROFILE, "The profile " + certificateProfile.toString() + " already exists");
	    }

	    log.debug("Save profile: " + certificateProfile.toString());
	    em.persist(certificateProfile);
	}
	else
	{
	    log.debug("Update profile: " + certificateProfile.toString());
	    em.merge(certificateProfile);
	}

	return (certificateProfile);
    }

    private CertificateProfile getProfile(String certificateProfileName)
    {
	return certificateProfileRepository.findByProfileName(certificateProfileName);
    }

}
