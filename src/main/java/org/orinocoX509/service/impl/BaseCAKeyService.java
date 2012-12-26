package org.orinocoX509.service.impl;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import lombok.Getter;
import lombok.Setter;

import org.orinocoX509.entity.CertificateProfile.KeySizeValues;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.CAKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public abstract class BaseCAKeyService implements CAKeyService
{
    private String keyStoreType;
    private String keyStoreProvider;
    private String keyStorePin;
    private String privateKeyAlias;
    private String publicKeyAlias;
    private String caCertificateAlias;

    private static final Logger log = LoggerFactory.getLogger(BaseCAKeyService.class);

    protected enum CRYPO_OBJECT
    {
	CERTIFICATE, PRIVATE_KEY, PUBLIC_KEY
    };
    
    public BaseCAKeyService(String keyStoreType, String keyStorePin, String privateKeyAlias, String publicKeyAlias, String caCertificateAlias, String keyStoreProvider, Provider provider)
    {
	this.keyStoreType = keyStoreType;
	this.keyStorePin = keyStorePin;
	this.privateKeyAlias = privateKeyAlias;
	this.publicKeyAlias = publicKeyAlias;
	this.caCertificateAlias = caCertificateAlias;
	this.keyStoreProvider = keyStoreProvider;
	Security.addProvider(provider);
    }

    
    @Override
    public String getProvider()
    {
	return (keyStoreProvider);
    }


    @Override
    public PrivateKey getCAPrivateKey()
    {
	return ((PrivateKey) getData(getPrivateKeyAlias(), CRYPO_OBJECT.PRIVATE_KEY));
    }

    @Override
    public PublicKey getCAPublicKey()
    {
	return ((PublicKey) getData(getPublicKeyAlias(), CRYPO_OBJECT.PUBLIC_KEY));
    }

    @Override
    public X509Certificate getCACertificate()
    {
	return ((X509Certificate) getData(getCaCertificateAlias(), CRYPO_OBJECT.CERTIFICATE));
    }

    protected abstract Object getData(String alias, CRYPO_OBJECT objectType);

    @Override
    public KeyPair generateKeyPair(KeySizeValues keySize)
    {
	KeyPair keyPair = null;
	try
	{
	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", getKeyStoreProvider());
	    keyPairGenerator.initialize(keySize.getValue());
	    keyPair = keyPairGenerator.generateKeyPair();
	}
	catch (NoSuchAlgorithmException exc)
	{
	    log.error("Cryptographic algorithm not supported while creating a key pair." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, "Cryptographic algorithm not supported while creating a key pair." + exc.getMessage());
	}
	catch (NoSuchProviderException exc)
	{
	    log.error("Cryptographic provider is not supported while creating a key pair." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_CRYPTOGRAPHIC_PROVIDER, "Cryptographic provider is not supported while creating a key pair." + exc.getMessage());
	}

	return keyPair;
    }

    @Override
    abstract public void storeCertificateCA(KeyPair keyPair, X509Certificate certificate);

}
