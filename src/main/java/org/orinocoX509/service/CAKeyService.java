package org.orinocoX509.service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.orinocoX509.entity.CertificateProfile.KeySizeValues;

public interface CAKeyService
{
    public String getProvider();

    public PrivateKey getCAPrivateKey();

    public PublicKey getCAPublicKey();

    public X509Certificate getCACertificate();
    
    public KeyPair generateKeyPair(KeySizeValues keySize);
    
    public void storeCertificateCA(KeyPair keyPair, X509Certificate certificate);
    
}
