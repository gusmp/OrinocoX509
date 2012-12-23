package org.orinocoX509.service.impl.sw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.impl.BaseCAKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CAKeyServiceSwImpl extends BaseCAKeyService
{
    private String KEYSTORE_PATH;
    private KeyStore store;

    private static final Logger log = LoggerFactory.getLogger(CAKeyServiceSwImpl.class);

    public CAKeyServiceSwImpl(String keyStorePath, String keyStoreType, String keyStorePin, String privateKeyAlias, String publicKeyAlias, String caCertificateAlias)
    {
	super();
	Security.addProvider(new BouncyCastleProvider());
	this.KEYSTORE_TYPE = keyStoreType;
	this.KEYSTORE_PROVIDER = "BC";
	this.KEYSTORE_PATH = keyStorePath;
	this.KEYSTORE_PIN = keyStorePin;
	this.PRIVATE_KEY_ALIAS = privateKeyAlias;
	this.PUBLIC_KEY_ALIAS = publicKeyAlias;
	this.CA_CERTIFICATE_ALIAS = caCertificateAlias;

	try
	{
	    File keyStoreFile = new File(KEYSTORE_PATH);
	    if (keyStoreFile.exists() == false)
	    {
		createKeyStore();
	    }
	    else
	    {
		loadKeyStore();
	    }

	}
	catch (NoSuchProviderException exc)
	{
	    log.error("The cryptographic provider " + KEYSTORE_PROVIDER + " does not exists." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_CRYPTOGRAPHIC_PROVIDER, "The cryptographic provider " + KEYSTORE_PROVIDER + " does not exists." + exc.getMessage());
	}
	catch (KeyStoreException exc)
	{
	    log.error("The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE, "The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
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
	catch (NoSuchAlgorithmException exc)
	{
	    log.error("Cryptographic algorithm not supported." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, "Cryptographic algorithm not supported." + exc.getMessage());
	}
    }

    private void createKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException
    {
	store = KeyStore.getInstance(KEYSTORE_TYPE);
	store.load(null, null);
    }

    private void loadKeyStore() throws NoSuchProviderException, KeyStoreException, FileNotFoundException, CertificateException, NoSuchAlgorithmException, IOException
    {
	FileInputStream fin = null;
	try
	{
	    store = KeyStore.getInstance(KEYSTORE_TYPE, KEYSTORE_PROVIDER);
	    FileInputStream keyStoreStream = new FileInputStream(KEYSTORE_PATH);
	    store.load(keyStoreStream, KEYSTORE_PIN.toCharArray());
	}
	finally
	{
	    if (fin != null)
	    {
		try
		{
		    fin.close();
		}
		catch (Exception exc)
		{
		}
		;
	    }
	}
    }

    @Override
    protected Object getData(String alias, CRYPO_OBJECT objectType)
    {
	try
	{
	    if (objectType == CRYPO_OBJECT.PRIVATE_KEY)
	    {
		return (store.getKey(alias, KEYSTORE_PIN.toCharArray()));
	    }
	    else if (objectType == CRYPO_OBJECT.CERTIFICATE)
	    {
		return ((X509Certificate) store.getCertificate(alias));
	    }
	    else if (objectType == CRYPO_OBJECT.PUBLIC_KEY)
	    {
		X509Certificate caCertificate = (X509Certificate) store.getCertificate(alias);
		return (caCertificate.getPublicKey());
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

	    FileOutputStream rootCertificate = new FileOutputStream(KEYSTORE_PATH);
	    store.store(rootCertificate, KEYSTORE_PIN.toCharArray());
	    rootCertificate.close();
	}

	catch (KeyStoreException exc)
	{
	    log.error("Error creating the key pair." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE, "Error creating the key pair." + exc.getMessage());
	}
	catch (IOException exc)
	{
	    log.error("Error saving the key pair in file system." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.IO_ERROR, "Error saving the key pair in file system." + exc.getMessage());
	}
	catch (CertificateException exc)
	{
	    log.error("The software keystore with the public/private key of the CA could not be initialized while creating the CA key pair." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.COULD_NOT_BE_INITIALIZED_THE_KEYSTORE, "The software keystore with the public/private key of the CA could not be initialized while creating the CA key pair." + exc.getMessage());
	}
	catch (NoSuchAlgorithmException exc)
	{
	    log.error("Cryptographic algorithm not supported while storing the CA key pair / root certificate." + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, "Cryptographic algorithm not supported while storing the CA key pair / root certificate." + exc.getMessage());
	}
    }
}
