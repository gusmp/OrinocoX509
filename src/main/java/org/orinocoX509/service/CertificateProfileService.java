package org.orinocoX509.service;

import java.util.List;

import org.orinocoX509.bean.CertificateValuesBean;
import org.orinocoX509.entity.CertificateProfile;

public interface CertificateProfileService
{
    public CertificateProfile saveProfile(CertificateProfile certificateProfile);

    public void deleteProfile(CertificateProfile certificateProfile);

    public CertificateProfile getProfile(CertificateProfile certificateProfile);

    public void validateProfile(CertificateProfile certificateProfile, CertificateValuesBean certificateValues);

    public List<CertificateProfile> getProfiles();
}
