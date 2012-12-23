package org.orinocoX509.service.impl;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.orinocoX509.entity.CertificateProfile.KeySizeValues;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.CAKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseCAKeyService implements CAKeyService
{
    protected String KEYSTORE_TYPE;
    protected String KEYSTORE_PROVIDER;
    protected String KEYSTORE_PIN;
    protected String PRIVATE_KEY_ALIAS;
    protected String PUBLIC_KEY_ALIAS;
    protected String CA_CERTIFICATE_ALIAS;
    
    private static final Logger log = LoggerFactory.getLogger(BaseCAKeyService.class);

    protected enum CRYPO_OBJECT
    {
	CERTIFICATE, PRIVATE_KEY, PUBLIC_KEY
    };

    @Override
    public String getProvider()
    {
	return (this.KEYSTORE_PROVIDER);
    }

    @Override
    public PrivateKey getCAPrivateKey()
    {
	return ((PrivateKey) getData(PRIVATE_KEY_ALIAS, CRYPO_OBJECT.PRIVATE_KEY));
    }

    @Override
    public PublicKey getCAPublicKey()
    {
	return ((PublicKey) getData(PUBLIC_KEY_ALIAS, CRYPO_OBJECT.PUBLIC_KEY));
    }

    @Override
    public X509Certificate getCACertificate()
    {
	return ((X509Certificate) getData(CA_CERTIFICATE_ALIAS, CRYPO_OBJECT.CERTIFICATE));
    }

    abstract protected Object getData(String alias, CRYPO_OBJECT objectType);

    @Override
    public KeyPair generateKeyPair(KeySizeValues keySize)
    {
	KeyPair keyPair = null;
	try
	{
	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER);
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
