package org.orinocoX509.holder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.orinocoX509.entity.field.certificate.FieldType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString(includeFieldNames=true)
public class CertificateValues 
{
	
	private Date notBefore;
	
	private Date notAfter;
	
	private String request;
	
	private Map<FieldType, Map<String,String>> values;
	
	public CertificateValues() 
	{
		this.values = new HashMap<FieldType, Map<String,String>>(5);
	} 

}
