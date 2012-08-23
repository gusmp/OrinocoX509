package org.orinocoX509.service;

import java.util.List;

import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;

public interface CertificateStatusService 
{
	public CertificateStatus saveStatus(CertificateStatus certificateStatus);
	public void deleteStatus(CertificateStatus certificateStatus);
	public CertificateStatus getStatus(CertificateStatus certificateStatus);
	public List<CertificateStatus> getCertificatesStatus(CertificateStatusValues certificateStatusValue);
}