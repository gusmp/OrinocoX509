package org.orinocoX509.service;

import java.util.List;

import org.orinocoX509.entity.CRLProfile;

public interface CRLProfileService 
{
	public CRLProfile saveProfile(CRLProfile crlProfile);
	public void deleteProfile(CRLProfile crlProfile);
	public CRLProfile getProfile(CRLProfile crlProfile);
	public void validateProfile(CRLProfile crlProfile);
	public CRLProfile updateCrlNumber(CRLProfile crlProfile);
	public List<CRLProfile> getProfiles();
}
