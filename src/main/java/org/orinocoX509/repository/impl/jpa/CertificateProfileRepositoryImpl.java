package org.orinocoX509.repository.impl.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.repository.CertificateProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


@Repository
public class CertificateProfileRepositoryImpl implements CertificateProfileRepository 
{
	@PersistenceContext
	private EntityManager em;
	
	private static final Logger log = LoggerFactory.getLogger(CertificateProfileRepositoryImpl.class);

	public CertificateProfile saveProfile(CertificateProfile certificateProfile) 
	{
		
		if (certificateProfile.getProfileId() == null)
		{
			if (getProfile(certificateProfile.getProfileName()) != null)
			{
				log.debug("The profile " +  certificateProfile.toString() + " alreday exists");
				throw new EngineException(EngineErrorCodes.DUPLICATE_PROFILE, "The profile " +  certificateProfile.toString() + " alreday exists");
			}
			
			log.debug("Save profile: " +  certificateProfile.toString());
			em.persist(certificateProfile);
		}
		else
		{
			log.debug("Update profile: " +  certificateProfile.toString());
			em.merge(certificateProfile);
		}
		
		return(certificateProfile);
	}

	public void deleteProfile(CertificateProfile certificateProfile) 
	{
		certificateProfile = em.merge(certificateProfile);
		log.debug("Delete profile: " +  certificateProfile.toString());
		em.remove(certificateProfile);
	}

	public CertificateProfile getProfile(CertificateProfile certificateProfile) 
	{
		// search by Id
		try
		{
			log.debug("Search profile: " +  certificateProfile.getProfileId());
			TypedQuery<CertificateProfile> q = em.createQuery("SELECT cp FROM CertificateProfile cp WHERE cp.profileId = :profileId", CertificateProfile.class);
			q.setParameter("profileId", certificateProfile.getProfileId());
			log.debug("Search profile: " +  certificateProfile.getProfileId() + " OK");
			return q.getSingleResult();
		}
		catch(NoResultException exc) 
		{
			log.debug("Search profile: " +  certificateProfile.getProfileId() + " No result");
			return(null);
		}
	}
	
	public CertificateProfile getProfile(String certificateProfileName) 
	{
		// search by Name
		try
		{
			log.debug("Search profile: " +  certificateProfileName);
			TypedQuery<CertificateProfile> q = em.createQuery("SELECT cp FROM CertificateProfile cp WHERE cp.profileName = :profileName", CertificateProfile.class);
			q.setParameter("profileName", certificateProfileName);
			log.debug("Search profile: " +  certificateProfileName + " OK");
			return q.getSingleResult();
		}
		catch(NoResultException exc) 
		{
			log.debug("Search profile: " +  certificateProfileName + " No result");
			return(null);
		}
	}

	public List<CertificateProfile> getProfiles() 
	{

		log.debug("Get all certificate profiles");
		TypedQuery<CertificateProfile> q = em.createQuery("SELECT cp FROM CertificateProfile cp ORDER BY cp.profileName", CertificateProfile.class);
		log.debug("Returned " + q.getResultList().size() + " profiles");
		return(q.getResultList());
	}
	
	
	
	
}
