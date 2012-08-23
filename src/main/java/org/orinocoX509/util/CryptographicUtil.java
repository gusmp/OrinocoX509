package org.orinocoX509.util;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.encoders.Base64;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CryptographicUtil 
{
	private static final Logger log = LoggerFactory.getLogger(CryptographicUtil.class);
	
	public String base64Encode(String data)
	{
		return(new String(Base64.encode(data.getBytes())));
	}
	
	public String base64Encode(byte[] data)
	{
		return(new String(Base64.encode(data)));
	}
	
	public String base64StringDecode(String data)
	{
		return(new String(Base64.decode(data.getBytes())));
	}
	
	public byte[] base64ByteDecode(String data)
	{
		return(Base64.decode(data));
	}
	
	public PublicKey getPublicKey(String request) throws EngineException
	{
		try
		{
			org.bouncycastle.pkcs.PKCS10CertificationRequest pkcs10CertificationRequest = new org.bouncycastle.pkcs.PKCS10CertificationRequest(base64ByteDecode(request));
			SubjectPublicKeyInfo subjectPKInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo(); 
			X509EncodedKeySpec xspec = new X509EncodedKeySpec(new DERBitString(subjectPKInfo).getBytes()); 
			AlgorithmIdentifier keyAlg = subjectPKInfo.getAlgorithm(); 
			return KeyFactory.getInstance(keyAlg.getAlgorithm().getId()).generatePublic(xspec); 
		}
		catch(Exception exc)
		{
			log.error("PKCS#10 has an invalid format.\nRequest: " + request + "\nDetails: " + exc.getMessage());
			throw new EngineException(EngineErrorCodes.WRONG_PKCS10_FORMAT, "PKCS#10 has an invalid format." + exc.getMessage());
		}
	}
	
	public String reverseDN(String dn)
	{
		String[] dnArray = dn.split(",");
		if (dnArray.length < 2)
		{
			return(dn);
		}
		else
		{
			String reverseDn = "";
			for(int i=dnArray.length-1; i>=0;i--)
			{
				if (i >0) { reverseDn += dnArray[i].trim() + ", "; }
				else {reverseDn += dnArray[i]; }
			}
			return(reverseDn);
		}
	}
}
