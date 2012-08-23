package org.orinocoX509.service;

import java.util.List;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.holder.CertificateValues;

public interface CertificateProfileService 
{
	public CertificateProfile saveProfile(CertificateProfile certificateProfile);
	public void deleteProfile(CertificateProfile certificateProfile);
	public CertificateProfile getProfile(CertificateProfile certificateProfile);
	public void validateProfile(CertificateProfile certificateProfile, CertificateValues certificateValues);
	public List<CertificateProfile> getProfiles();
}
