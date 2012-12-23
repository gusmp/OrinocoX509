package org.orinocoX509.service.impl.hsm;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.impl.BaseCAKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CAKeyServiceGenericHSM extends BaseCAKeyService
{
    private KeyStore store;

    private static final Logger log = LoggerFactory.getLogger(CAKeyServiceGenericHSM.class);

    public CAKeyServiceGenericHSM(Provider provider, String keyStoreType, String keyStoreProvider, String keyStorePin, String privateKeyAlias, String publicKeyAlias, String caCertificateAlias)
    {
	super();
	Security.addProvider(provider);
	this.KEYSTORE_TYPE = keyStoreType;
	this.KEYSTORE_PROVIDER = keyStoreProvider;
	this.KEYSTORE_PIN = keyStorePin;
	this.PRIVATE_KEY_ALIAS = privateKeyAlias;
	this.PUBLIC_KEY_ALIAS = publicKeyAlias;
	this.CA_CERTIFICATE_ALIAS = caCertificateAlias;

	// load key store
	try
	{
	    store = KeyStore.getInstance(KEYSTORE_TYPE, KEYSTORE_PROVIDER);
	    store.load(null, null);
	}
	catch (NoSuchProviderException exc)
	{
	    log.error("The cryptographic provider " + KEYSTORE_PROVIDER + " does not exists." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_CRYPTOGRAPHIC_PROVIDER, "The cryptographic provider " + KEYSTORE_PROVIDER + " does not exists." + exc.getMessage());
	}
	catch (CertificateException exc)
	{
	    log.error("The keystore with the public/private key of the CA could not be initialized." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.COULD_NOT_BE_INITIALIZED_THE_KEYSTORE, "The keystore with the public/private key of the CA could not be initialized." + exc.getMessage());
	}
	catch (IOException exc)
	{
	    log.error("The keystore with the public/private key of the CA can not be accessed." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.IO_ERROR, "The keystore with the public/private key of the CA can not be accessed." + exc.getMessage());
	}
	catch (KeyStoreException exc)
	{
	    log.error("The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE, "The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
	}
	catch (NoSuchAlgorithmException exc)
	{
	    log.error("Cryptographic algorithm not supported." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, "Cryptographic algorithm not supported." + exc.getMessage());
	}
    }

    @Override
    protected Object getData(String alias, CRYPO_OBJECT objectType)
    {
	try
	{
	    if (objectType == CRYPO_OBJECT.PRIVATE_KEY)
	    {
		return ((PrivateKey) store.getKey(PRIVATE_KEY_ALIAS, KEYSTORE_PIN.toCharArray()));
	    }
	    else if (objectType == CRYPO_OBJECT.CERTIFICATE)
	    {
		return ((X509Certificate) store.getCertificate(CA_CERTIFICATE_ALIAS));
	    }
	    else if (objectType == CRYPO_OBJECT.PUBLIC_KEY)
	    {
		return ((PublicKey) store.getKey(PUBLIC_KEY_ALIAS, null));
	    }
	    else
	    {
		log.error("No key with alias " + alias + " and type" + objectType.name() + " was found!!");
		return (null);
	    }
	}
	catch (KeyStoreException exc)
	{
	    log.error("The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE, "The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
	}
	catch (UnrecoverableKeyException exc)
	{
	    log.error("The key " + alias + " could not be found." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.KEY_NOT_FOUND, "The key " + alias + " could not be found." + exc.getMessage());
	}
	catch (NoSuchAlgorithmException exc)
	{
	    log.error("Cryptographic algorithm not supported." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, "Cryptographic algorithm not supported." + exc.getMessage());
	}
    }

    @Override
    public void storeCertificateCA(KeyPair keyPair, X509Certificate certificate)
    {
	try
	{
	    Certificate certificateChain[] = new Certificate[1];
	    Arrays.asList(certificate).toArray(certificateChain);
	    store.setKeyEntry(PRIVATE_KEY_ALIAS, keyPair.getPrivate(), KEYSTORE_PIN.toCharArray(), certificateChain);
	    store.setCertificateEntry(CA_CERTIFICATE_ALIAS, certificate);
	}
	catch (KeyStoreException exc)
	{
	    log.error("Error creating the key pair." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE, "Error creating the key pair." + exc.getMessage());
	}
    }

}
