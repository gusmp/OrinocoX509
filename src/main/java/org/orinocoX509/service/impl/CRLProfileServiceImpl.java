package org.orinocoX509.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.field.crl.AuthorityKeyIdentifierCRLField;
import org.orinocoX509.entity.field.crl.CRLFieldType;
import org.orinocoX509.entity.field.crl.CRLNumberField;
import org.orinocoX509.entity.field.crl.IssuingDistributionPointField;
import org.orinocoX509.entity.field.crl.TimeNextUpdateField;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.repository.CRLProfileRepository;
import org.orinocoX509.service.CRLProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CRLProfileServiceImpl implements CRLProfileService
{
    @Autowired
    CRLProfileRepository crlProfileRepository;

    private static final Logger log = LoggerFactory.getLogger(CRLProfileServiceImpl.class);

    @Override
    @CacheEvict(value = "crlProfile", key = "#crlProfile.profileId")
    @Transactional(readOnly = false)
    public CRLProfile saveProfile(CRLProfile crlProfile)
    {
	log.debug("Save CRL profile " + crlProfile.toString());
	return (crlProfileRepository.saveProfile(crlProfile));
    }

    @Override
    @CacheEvict(value = "crlProfile", key = "#crlProfile.profileId")
    @Transactional(readOnly = false)
    public void deleteProfile(CRLProfile crlProfile)
    {
	log.debug("Delete CRL profile " + crlProfile.toString());
	crlProfileRepository.deleteProfile(crlProfile);
    }

    @Override
    @Cacheable(value = "crlProfile", key = "#crlProfile.profileId")
    @Transactional(readOnly = true)
    public CRLProfile getProfile(CRLProfile crlProfile)
    {
	log.debug("Get CRL profile " + crlProfile.getProfileId());
	return (crlProfileRepository.getProfile(crlProfile));
    }

    @Override
    public void validateProfile(CRLProfile crlProfile) throws EngineException
    {
	TimeNextUpdateField timeNextUpdateField = (TimeNextUpdateField) crlProfile.getField(CRLFieldType.TIME_NEXT_UPDATE);
	if (timeNextUpdateField != null)
	{
	    if (timeNextUpdateField.getDaysNextUpdate() < 1)
	    {
		throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, "The time for the next update must be greater than 0");
	    }
	}

	AuthorityKeyIdentifierCRLField authorityKeyIdentifier = (AuthorityKeyIdentifierCRLField) crlProfile.getField(CRLFieldType.AUTHORITY_KEY_IDENTIFIER);
	if (authorityKeyIdentifier != null)
	{
	    if ((authorityKeyIdentifier.getAuthorityKeyIdentifier() == false) && (authorityKeyIdentifier.getAuthorityIssuerSerialNumberCertificate() == false))
	    {
		throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, "To add the authority key identifier extension, key identifier or issuer and serial number must be true");
	    }
	}

	IssuingDistributionPointField crlDistributionPointField = (IssuingDistributionPointField) crlProfile.getField(CRLFieldType.ISSUING_DISTRIBUTION_POINT);
	if (crlDistributionPointField != null)
	{
	    if (crlDistributionPointField.getValues().size() == 0)
	    {
		throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, "CRL distributions points extension requires a value, but in the profile there is not");
	    }
	}
    }

    @Override
    @Transactional(readOnly = true)
    public CRLProfile updateCrlNumber(CRLProfile crlProfile)
    {
	CRLNumberField crlNumberField = (CRLNumberField) crlProfile.getField(CRLFieldType.CRL_NUMBER);
	crlNumberField.setCrlNumber(crlNumberField.getCrlNumber().add(BigInteger.ONE));
	crlProfileRepository.saveProfile(crlProfile);
	return (crlProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CRLProfile> getProfiles()
    {
	return (crlProfileRepository.getProfiles());
    }

}
