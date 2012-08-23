package org.orinocoX509.repository;

import java.util.List;

import org.orinocoX509.entity.CRLProfile;

public interface CRLProfileRepository 
{
	public CRLProfile saveProfile(CRLProfile crlProfile);
	public void deleteProfile(CRLProfile crlProfile);
	public CRLProfile getProfile(CRLProfile crlProfile);
	public List<CRLProfile> getProfiles();
}
