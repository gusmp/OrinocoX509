package org.orinocoX509.service;


public interface HttpService 
{
	public byte[] sendData(byte[] data, String mime, String target);
	public byte[] downloadData(String target);
}
