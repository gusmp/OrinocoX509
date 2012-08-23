package org.orinocoX509.service;

import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;


public interface CRLService 
{
	public X509CRL generateCRL(CRLProfile crlProfile);
	public CertificateStatusValues getStatus(BigInteger serialNumber, List<String> crlDistrubutionPoints);
	public CertificateStatusValues getStatus(X509Certificate certificate);
	public CertificateStatusValues getStatus(X509Certificate certificate, List<String> crlDistrubutionPoints);
}