package org.orinocoX509.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PKCS10Util
{
    @Autowired
    private Base64Util base64Util;

    private static final Logger log = LoggerFactory.getLogger(PKCS10Util.class);

    public static PKCS10CertificationRequest generateRequest(X500Name subject, KeyPair keyPair) throws OperatorCreationException
    {
	PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
	PKCS10CertificationRequest request = requestBuilder.build(new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate()));
	return (request);
    }

    public PublicKey getPublicKey(String request) throws EngineException
    {
	try
	{
	    // org.bouncycastle.pkcs.PKCS10CertificationRequest
	    // pkcs10CertificationRequest = new
	    // org.bouncycastle.pkcs.PKCS10CertificationRequest(base64ByteDecode(request));
	    PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(base64Util.base64ByteDecode(request));
	    SubjectPublicKeyInfo subjectPKInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
	    X509EncodedKeySpec xspec = new X509EncodedKeySpec(new DERBitString(subjectPKInfo).getBytes());
	    AlgorithmIdentifier keyAlg = subjectPKInfo.getAlgorithm();
	    return KeyFactory.getInstance(keyAlg.getAlgorithm().getId()).generatePublic(xspec);
	}
	catch (Exception exc)
	{
	    log.error("PKCS#10 has an invalid format.\nRequest: " + request + "\nDetails: " + exc.getMessage());
	    throw new EngineException(EngineErrorCodes.WRONG_PKCS10_FORMAT, "PKCS#10 has an invalid format." + exc.getMessage());
	}
    }

}
