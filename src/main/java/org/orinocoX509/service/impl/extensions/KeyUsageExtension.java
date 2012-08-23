package org.orinocoX509.service.impl.extensions;

import java.util.List;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.value.certificate.CertificateFieldValue;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue;
import org.orinocoX509.entity.value.certificate.KeyUsageFieldValue.KeyUsageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyUsageExtension implements EngineExtension
{
	private List<CertificateFieldValue> values;
	private Boolean critical;
	private static final Logger log = LoggerFactory.getLogger(KeyUsageExtension.class);
	
	public KeyUsageExtension(List<CertificateFieldValue> values, Boolean critical)
	{
		this.values = values;
		this.critical = critical;
	}
	
	public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
	{
		int keyUsages = 0;
		
		for(int i=0; i < this.values.size(); i++)
		{
			KeyUsageFieldValue keyUsageFieldValue = (KeyUsageFieldValue) this.values.get(i);
			if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.DIGITAL_SIGNATURE)
			{
				keyUsages = keyUsages | KeyUsage.digitalSignature;
			}
			else if  (keyUsageFieldValue.getKeyUsage() == KeyUsageType.NON_REPUTATION)
			{
				keyUsages = keyUsages | KeyUsage.nonRepudiation;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.KEY_ENCIPHERMENT)
			{
				keyUsages = keyUsages | KeyUsage.keyEncipherment;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.DATA_ENCIPHERMENT)
			{
				keyUsages = keyUsages | KeyUsage.dataEncipherment;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.KEY_AGREEMENT)
			{
				keyUsages = keyUsages | KeyUsage.keyAgreement;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.KEY_CERT_SIGN)
			{
				keyUsages = keyUsages | KeyUsage.keyCertSign;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.CRL_SIGN)
			{
				keyUsages = keyUsages | KeyUsage.cRLSign;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.ENCIPHER_ONLY)
			{
				keyUsages = keyUsages | KeyUsage.encipherOnly;
			}
			else if (keyUsageFieldValue.getKeyUsage() == KeyUsageType.DECIPHER_ONLY)
			{
				keyUsages = keyUsages | KeyUsage.decipherOnly;
			}
		}
		
		log.debug("KeyUsage to apply " + keyUsages + " Critical: " + critical.booleanValue());
		certificateGenerator.addExtension(X509Extension.keyUsage, critical.booleanValue(), new KeyUsage(keyUsages));
		
		return(certificateGenerator);
	}
}
