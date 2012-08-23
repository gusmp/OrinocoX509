package org.orinocoX509.repository.impl.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.repository.CRLProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


@Repository
public class CRLProfileRepositoryImpl implements CRLProfileRepository 
{
	@PersistenceContext
	private EntityManager em;
	
	private static final Logger log = LoggerFactory.getLogger(CRLProfileRepositoryImpl.class);

	public CRLProfile saveProfile(CRLProfile crlProfile) 
	{
		
		if (crlProfile.getProfileId() == null)
		{
			if (getProfile(crlProfile.getProfileName()) != null)
			{
				log.debug("The profile " +  crlProfile.toString() + " alreday exists");
				throw new EngineException(EngineErrorCodes.DUPLICATE_PROFILE, "The profile " +  crlProfile.toString() + " alreday exists");
			}
			
			log.debug("Save CRL profile: " +  crlProfile.toString());
			em.persist(crlProfile);
		}
		else
		{
			log.debug("Update CRL profile: " +  crlProfile.toString());
			em.merge(crlProfile);
		}
		
		return(crlProfile);
	}

	public void deleteProfile(CRLProfile crlProfile) 
	{
		crlProfile = em.merge(crlProfile);
		log.debug("Delete CRL profile: " +  crlProfile.toString());
		em.remove(crlProfile);
	}

	public CRLProfile getProfile(CRLProfile crlProfile) 
	{
		// search by Id
		try
		{
			log.debug("Search CRL profile: " +  crlProfile.getProfileId());
			TypedQuery<CRLProfile> q = em.createQuery("SELECT cp FROM CRLProfile cp WHERE cp.profileId = :profileId", CRLProfile.class);
			q.setParameter("profileId", crlProfile.getProfileId());
			log.debug("Search CRL profile: " +  crlProfile.getProfileId() + " OK");
			return q.getSingleResult();
		}
		catch(NoResultException exc) 
		{
			log.debug("Search CRL profile: " +  crlProfile.getProfileId() + " No result");
			return(null);
		}
	}
	
	public CRLProfile getProfile(String crlProfileName) 
	{
		// search by Id
		try
		{
			log.debug("Search CRL profile: " +  crlProfileName);
			TypedQuery<CRLProfile> q = em.createQuery("SELECT cp FROM CRLProfile cp WHERE cp.profileName = :profileName", CRLProfile.class);
			q.setParameter("profileName", crlProfileName);
			log.debug("Search CRL profile: " +  crlProfileName + " OK");
			return q.getSingleResult();
		}
		catch(NoResultException exc) 
		{
			log.debug("Search CRL profile: " + crlProfileName + " No result");
			return(null);
		}
	}

	public List<CRLProfile> getProfiles() 
	{
		log.debug("Get all CRL profiles");
		TypedQuery<CRLProfile> q = em.createQuery("SELECT cp FROM CRLProfile cp ORDER BY cp.profileName", CRLProfile.class);
		log.debug("Returned " + q.getResultList().size() + " profiles");
		return(q.getResultList());
	}
	
	
	
	
}
