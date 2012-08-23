package org.orinocoX509.service.impl.sw;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.CAKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CAKeyServiceSwImpl implements CAKeyService  
{

	private String KEYSTORE_TYPE;
	private String KEYSTORE_PROVIDER;
	private String KEYSTORE_PATH;
	private String KEYSTORE_PIN;
	private String PRIVATE_KEY_ALIAS;
	private String PUBLIC_KEY_ALIAS;
	private String CA_CERTIFICATE_ALIAS;
	private KeyStore store;
	
	private enum CRYPO_OBJECT {CERTIFICATE, PRIVATE_KEY, PUBLIC_KEY};
	
	private static final Logger log = LoggerFactory.getLogger(CAKeyServiceSwImpl.class);
	
	public CAKeyServiceSwImpl(String keyStorePath, String keyStoreType, String keyStorePin, 
			String privateKeyAlias, String publicKeyAlias, String caCertificateAlias)
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
		
		FileInputStream fin = null;
		
		try
		{
			store = KeyStore.getInstance(KEYSTORE_TYPE, KEYSTORE_PROVIDER);
			fin = new FileInputStream(KEYSTORE_PATH);
			store.load(fin, KEYSTORE_PIN.toCharArray());
		}
		catch(NoSuchProviderException exc)
		{
			log.error("The cryptographic provider " + KEYSTORE_PROVIDER  +" does not exists." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.UNKNOWN_CRYPTOGRAPHIC_PROVIDER , 
					"The cryptographic provider " + KEYSTORE_PROVIDER  +" does not exists." + exc.getMessage());
		}
		catch(KeyStoreException exc)
		{
			log.error("The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE , 
					"The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
		}
		catch(CertificateException exc)
		{
			log.error("The keystore with the public/private key of the CA could not be initialized." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.COULD_NOT_BE_INITIALIZED_THE_KEYSTORE, 
					"The keystore with the public/private key of the CA could not be initialized." + exc.getMessage());
		}
		catch(IOException exc)
		{
			log.error("The keystore with the public/private key of the CA can not be accessed." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.IO_ERROR, 
					"The keystore with the public/private key of the CA can not be accessed." + exc.getMessage());
		}
		catch(NoSuchAlgorithmException exc)
		{
			log.error("Cryptographic algorithm not supported." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, 
					"Cryptographic algorithm not supported." + exc.getMessage());
		}
		finally
		{
			if (fin != null)
			{
				try { fin.close(); } catch(Exception exc) {};
			}
		}
	}
	
	public String getProvider()
	{
		return(this.KEYSTORE_PROVIDER);
	}
	
	private Object getData(String alias, CRYPO_OBJECT objectType)
	{
		try
		{
			if (objectType == CRYPO_OBJECT.PRIVATE_KEY)
			{
				return(store.getKey(alias, KEYSTORE_PIN.toCharArray()));
			}
			else if (objectType == CRYPO_OBJECT.CERTIFICATE)
			{
				return((X509Certificate)store.getCertificate(alias));
			}
			else if (objectType == CRYPO_OBJECT.PUBLIC_KEY)
			{
				X509Certificate caCertificate = (X509Certificate)store.getCertificate(alias);
				return(caCertificate.getPublicKey());
			}
			else
			{
				log.error("No key with alias " + alias + " and type" + objectType.name() + " was found!!");
				return(null);
			}
		}
		catch(KeyStoreException exc)
		{
			log.error("The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.UNKNOWN_KEYSTORE_TYPE , 
					"The key store type " + KEYSTORE_TYPE + " is not known." + exc.getMessage());
		}
		catch(UnrecoverableKeyException exc)
		{
			log.error("The key " + alias + " could not be found." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.KEY_NOT_FOUND , 
					"The key " + alias + " could not be found." + exc.getMessage());
		}
		catch(NoSuchAlgorithmException exc)
		{
			log.error("Cryptographic algorithm not supported." + exc.getMessage());
			throw new EngineException(EngineErrorCodes.CRYPTOGRAPHIC_ALGORITHM_NOT_SUPPORTED, 
					"Cryptographic algorithm not supported." + exc.getMessage());
		}
	}
	
	public PrivateKey getCAPrivateKey()
	{
		return((PrivateKey) getData(PRIVATE_KEY_ALIAS, CRYPO_OBJECT.PRIVATE_KEY));
	}
	
	public PublicKey getCAPublicKey()
	{
		return((PublicKey) getData(PUBLIC_KEY_ALIAS, CRYPO_OBJECT.PUBLIC_KEY));
	}
	
	public X509Certificate getCACertificate()
	{
		return((X509Certificate) getData(CA_CERTIFICATE_ALIAS, CRYPO_OBJECT.CERTIFICATE));
	}
}
