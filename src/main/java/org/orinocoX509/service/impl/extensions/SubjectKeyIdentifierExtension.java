package org.orinocoX509.service.impl.extensions;


import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.orinocoX509.entity.field.certificate.CertificateField;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;


public class SubjectKeyIdentifierExtension implements EngineExtension
{
	private CertificateField certificateField;
	private PublicKey publicKey;
	
	public SubjectKeyIdentifierExtension(CertificateField certificateField, PublicKey publicKey)
	{
		this.certificateField = certificateField;
		this.publicKey = publicKey;
	}

	public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException 
	{
		try
		{
			SubjectKeyIdentifier subjectKeyId = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey);
			certificateGenerator.addExtension(X509Extension.subjectKeyIdentifier, certificateField.getCritical(), subjectKeyId);
			
			return(certificateGenerator);
		}
		
		catch(NoSuchAlgorithmException exc)
		{
			throw new EngineException(EngineErrorCodes.INVALID_PUBLIC_KEY_TYPE, "Error creating the SubjectKeyIdentifier field." + exc.getMessage());
		}
	}
}
