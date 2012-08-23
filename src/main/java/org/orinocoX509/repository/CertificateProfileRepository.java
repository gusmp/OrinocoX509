package org.orinocoX509.repository;


import java.util.List;

import org.orinocoX509.entity.CertificateProfile;

public interface CertificateProfileRepository 
{
	public CertificateProfile saveProfile(CertificateProfile certificateProfile);
	public void deleteProfile(CertificateProfile certificateProfile);
	public CertificateProfile getProfile(CertificateProfile certificateProfile);
	public List<CertificateProfile> getProfiles();
}
