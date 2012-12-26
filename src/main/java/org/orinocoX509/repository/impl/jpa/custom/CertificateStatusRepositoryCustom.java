package org.orinocoX509.repository.impl.jpa.custom;

import org.orinocoX509.entity.CertificateStatus;

public interface CertificateStatusRepositoryCustom
{
    public CertificateStatus saveStatus(CertificateStatus certificateStatus);

    public CertificateStatus getStatus(CertificateStatus certificateStatus);
}
