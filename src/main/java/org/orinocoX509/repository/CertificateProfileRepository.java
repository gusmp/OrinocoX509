package org.orinocoX509.repository;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.repository.impl.jpa.custom.CertificateProfileRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateProfileRepository extends JpaRepository<CertificateProfile, Integer>, CertificateProfileRepositoryCustom
{
    public CertificateProfile findByProfileName(String profileName);

    public CertificateProfile findByProfileIdOrProfileName(Integer profileId, String profileName);
}
