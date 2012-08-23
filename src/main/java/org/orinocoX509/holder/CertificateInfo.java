package org.orinocoX509.holder;

import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString(includeFieldNames=true)
public class CertificateInfo 
{

	private String     pemCertificate;
	private BigInteger serialNumber;
	private Date       notAfter;
	private Date       notBefore;
	
	public CertificateInfo()  { } 

}
