package org.orinocoX509.repository;

import org.orinocoX509.entity.CRLProfile;

import org.orinocoX509.repository.impl.jpa.custom.CRLProfileRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CRLProfileRepository extends JpaRepository<CRLProfile, Integer>, CRLProfileRepositoryCustom
{
    public CRLProfile findByProfileName(String profileName);

    public CRLProfile findByProfileIdOrProfileName(Integer profileId, String profileName);
}
