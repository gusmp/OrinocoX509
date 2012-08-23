package org.orinocoX509.service;

import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.holder.CertificateInfo;
import org.orinocoX509.holder.CertificateValues;

public interface CertificateService 
{
	public CertificateInfo generateCertificate(CertificateProfile certificateProfile, CertificateValues certificateValues);
	
}
