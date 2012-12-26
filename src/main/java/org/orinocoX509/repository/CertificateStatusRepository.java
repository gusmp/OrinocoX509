package org.orinocoX509.repository;

import java.math.BigInteger;
import java.util.List;

import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.repository.impl.jpa.custom.CertificateStatusRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateStatusRepository extends JpaRepository<CertificateStatus, Integer>, CertificateStatusRepositoryCustom
{
    CertificateStatus findByCertificateSerialNumber(BigInteger certificateSerialNumber);

    List<CertificateStatus> findByCertificateStatus(CertificateStatusValues certificateStatusValue);
}