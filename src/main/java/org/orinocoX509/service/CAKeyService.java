package org.orinocoX509.service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface CAKeyService 
{
	public String getProvider();
	public PrivateKey getCAPrivateKey();
	public PublicKey getCAPublicKey();
	public X509Certificate getCACertificate();
}
