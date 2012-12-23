package org.orinocoX509.util;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Component;

@Component
public class Base64Util
{
    public String base64Encode(String data)
    {
	return (new String(Base64.encode(data.getBytes())));
    }

    public String base64Encode(byte[] data)
    {
	return (new String(Base64.encode(data)));
    }

    public String base64StringDecode(String data)
    {
	return (new String(Base64.decode(data.getBytes())));
    }

    public byte[] base64ByteDecode(String data)
    {
	return (Base64.decode(data));
    }

}
