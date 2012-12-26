package org.orinocoX509.service.impl;

import java.util.List;

import org.orinocoX509.bean.CertificateValuesBean;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.field.certificate.FieldType;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.repository.CertificateProfileRepository;
import org.orinocoX509.service.CertificateProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificateProfileServiceImpl implements CertificateProfileService
{
    @Autowired
    CertificateProfileRepository certificateProfileRepository;

    private static final Logger log = LoggerFactory.getLogger(CertificateProfileServiceImpl.class);

    @Override
    @CacheEvict(value = "certificateProfile", key = "#certificateProfile.profileId")
    @Transactional(readOnly = false)
    public CertificateProfile saveProfile(CertificateProfile certificateProfile)
    {
	log.debug("Save profile " + certificateProfile.toString());
	return (certificateProfileRepository.saveProfile(certificateProfile));
    }

    @Override
    @CacheEvict(value = "certificateProfile", key = "#certificateProfile.profileId")
    @Transactional(readOnly = false)
    public void deleteProfile(CertificateProfile certificateProfile)
    {
	log.debug("Delete profile " + certificateProfile.toString());
	certificateProfileRepository.delete(certificateProfile);
    }

    @Override
    @Cacheable(value = "certificateProfile", key = "#certificateProfile.profileId")
    @Transactional(readOnly = true)
    public CertificateProfile getProfile(CertificateProfile certificateProfile)
    {
	log.debug("Get profile " + certificateProfile.getProfileId());
	return certificateProfileRepository.findByProfileIdOrProfileName(certificateProfile.getProfileId(), certificateProfile.getProfileName());
    }

    @Override
    public void validateProfile(CertificateProfile certificateProfile, CertificateValuesBean certificateValues) throws EngineException
    {
	log.debug("Validate profile " + certificateProfile.getProfileId());
	checkProfile(certificateProfile);
	checkValues(certificateValues, certificateProfile);
	log.debug("Profile " + certificateProfile.getProfileId() + " is valid");
    }

    private void checkProfile(CertificateProfile profile) throws EngineException
    {
	Integer years = profile.getYears();
	if ((years == null) || (years < 1))
	{
	    throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, "The number of years must be an integer and greater than 0");
	}

	if (profile.getField(FieldType.SUBJECT) == null)
	{
	    throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, "The field subject is missing");
	}

	if (profile.getField(FieldType.ISSUER) == null)
	{
	    throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, "The field issuer is missing");
	}

	checkParameterNumber(profile, FieldType.AUTHORITY_INFORMATION_ACCESS, 1);
	checkParameterNumber(profile, FieldType.AUTHORITY_KEY_IDENTIFIER, 1);
	checkParameterNumber(profile, FieldType.CRL_DISTRIBUTION_POINT, 1);
	checkParameterNumber(profile, FieldType.EXTENDED_KEY_USAGE, 1);
	checkParameterNumber(profile, FieldType.ISSUER_ALTERNATIVE_NAME, 1);
	checkParameterNumber(profile, FieldType.KEY_USAGE, 1);
	checkParameterNumber(profile, FieldType.NETSCAPE_CERTIFICATE_TYPE, 1);
	checkParameterNumber(profile, FieldType.QUALIFIED_CERTIFICATE_STATEMENT, 1);
	checkParameterNumber(profile, FieldType.SUBJECT_ALTERNATIVE_NAME, 1);
	checkParameterNumber(profile, FieldType.SUBJECT_DIRECTORY_ATTRIBUTE, 1);

	return;
    }

    private void checkParameterNumber(CertificateProfile profile, FieldType fieldType, Integer parameterNumber) throws EngineException
    {
	BaseCertificateField field = profile.getField(fieldType);
	if (field != null)
	{
	    if (field.getValues().size() < parameterNumber)
	    {
		throw new EngineException(EngineErrorCodes.PROFILE_MALFORMED, fieldType.name() + " requires at least" + parameterNumber + " values");
	    }
	}
    }

    private void checkValues(CertificateValuesBean values, CertificateProfile profile) throws EngineException
    {
	if (values == null)
	{
	    throw new EngineException(EngineErrorCodes.WRONG_CERTIFICATE_VALUES, "The CertificateValues must not be null. Request missing!");
	}

	if ((values.getRequest() == null) || (values.getRequest().length() == 0))
	{
	    throw new EngineException(EngineErrorCodes.PKCS10_REQUEST_MISSING, "The PKCS#10 request is empty");
	}
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateProfile> getProfiles()
    {
	return  (certificateProfileRepository.findAll());
    }

}
