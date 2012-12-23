package org.orinocoX509.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpServiceImpl implements HttpService
{

    private static final Logger log = LoggerFactory.getLogger(HttpServiceImpl.class);
    private final int BUFFER_SIZE = 5000;

    @Override
    public byte[] sendData(byte[] data, String mime, String target) throws EngineException
    {
	try
	{
	    URL url = new URL(target);
	    URLConnection con = url.openConnection();
	    con.setDoInput(true);
	    con.setDoOutput(true);

	    con.setRequestProperty("Content-Type", mime);
	    con.connect();
	    OutputStream outStream = con.getOutputStream();
	    outStream.write(data);

	    InputStream inputStream = con.getInputStream();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    int byteRead;
	    byte[] buffer = new byte[BUFFER_SIZE];

	    while ((byteRead = inputStream.read(buffer)) != -1)
	    {
		byteArrayOutputStream.write(buffer, 0, byteRead);
	    }

	    return (byteArrayOutputStream.toByteArray());
	}
	catch (MalformedURLException exc)
	{
	    log.error(target + " is not a valid url\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.WRONG_URL, target + " is not a valid url\nDetails:\n" + exc.toString());
	}
	catch (IOException exc)
	{
	    log.error("It was impossible to connect to " + target + "\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.CONNECTION_REFUSED, "It was impossible to connect to " + target + "\nDetails:\n" + exc.toString());
	}
    }

    @Override
    public byte[] downloadData(String target) throws EngineException
    {
	try
	{
	    log.debug("Downloading " + target);
	    long startTime = System.currentTimeMillis();
	    URL url = new URL(target);
	    InputStream inStream = url.openStream();

	    byte[] buffer = new byte[BUFFER_SIZE];
	    ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();

	    int sizeRead = 0;
	    while ((sizeRead = inStream.read(buffer)) != -1)
	    {
		dataBuffer.write(buffer, 0, sizeRead);
	    }

	    inStream.close();

	    long endTime = System.currentTimeMillis();
	    log.debug("Time for downloading " + target + " is " + ((endTime - startTime) / 1000));

	    return (dataBuffer.toByteArray());
	}
	catch (MalformedURLException exc)
	{
	    log.error(target + " is not a valid url\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.WRONG_URL, target + " is not a valid url\nDetails:\n" + exc.toString());
	}
	catch (IOException exc)
	{
	    log.error("It was impossible to connect to " + target + " for downloading" + target + "\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.CONNECTION_REFUSED, "It was impossible to connect to " + target + " for downloading " + target + "\nDetails:\n" + exc.toString());
	}
    }
}
