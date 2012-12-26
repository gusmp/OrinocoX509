package org.orinocoX509.repository.impl.jpa.custom.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.repository.CRLProfileRepository;
import org.orinocoX509.repository.impl.jpa.custom.CRLProfileRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CRLProfileRepositoryImpl implements CRLProfileRepositoryCustom
{
    @Autowired
    private CRLProfileRepository crlProfileRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(CRLProfileRepositoryImpl.class);

    @Override
    public CRLProfile saveProfile(CRLProfile crlProfile)
    {

	if (crlProfile.getProfileId() == null)
	{
	    if (getProfile(crlProfile.getProfileName()) != null)
	    {
		log.debug("The profile " + crlProfile.toString() + " alreday exists");
		throw new EngineException(EngineErrorCodes.DUPLICATE_PROFILE, "The profile " + crlProfile.toString() + " alreday exists");
	    }

	    log.debug("Save CRL profile: " + crlProfile.toString());
	    em.persist(crlProfile);
	}
	else
	{
	    log.debug("Update CRL profile: " + crlProfile.toString());
	    em.merge(crlProfile);
	}

	return (crlProfile);
    }

    private CRLProfile getProfile(String crlProfileName)
    {
	return crlProfileRepository.findByProfileName(crlProfileName);
    }

}
