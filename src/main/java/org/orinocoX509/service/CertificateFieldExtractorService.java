package org.orinocoX509.service;

import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateFieldExtractorService 
{
	public String getOcspUrl(X509Certificate certificate);
	public List<String> getCRLDistributionsPoints(X509Certificate certificate);
	
}
