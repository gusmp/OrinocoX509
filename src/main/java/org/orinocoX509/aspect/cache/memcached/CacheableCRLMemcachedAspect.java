package org.orinocoX509.aspect.cache.memcached;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.spy.memcached.MemcachedClient;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class CacheableCRLMemcachedAspect
{
    @Autowired
    private MemcachedClient memcachedClient;

    private final String CRL_LABEL = "orinocoX509_crl";
    private final int RECOVERY_TIMEOUT = 2;

    private static final Logger log = LoggerFactory.getLogger(CacheableCRLMemcachedAspect.class);

    public CacheableCRLMemcachedAspect()
    {
    }

    @Pointcut("execution(* org.orinocoX509.service.CRLService.generateCRL(..))")
    public void cacheableCRLPointcutExpression()
    {
    }

    @Around("cacheableCRLPointcutExpression()")
    public Object loadCacheAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
	Object cachedData = getCachedData(proceedingJoinPoint);
	if (cachedData != null)
	{
	    return (cachedData);
	}

	X509CRL crl = (X509CRL) proceedingJoinPoint.proceed();
	setData(crl.getEncoded());
	return (crl);
    }

    @Pointcut("execution(* org.orinocoX509.service.CertificateStatusService.saveStatus(..))")
    public void cacheEvictCRLPointcutExpression()
    {
    }

    @After("cacheEvictCRLPointcutExpression()")
    public void evictCacheAspect(JoinPoint joinPoint) throws Throwable
    {
	setData(null);
    }

    private Object getCachedData(ProceedingJoinPoint proceedingJoinPoint)
    {
	long startTime = System.currentTimeMillis();

	Future<Object> requestor = memcachedClient.asyncGet(CRL_LABEL);

	try
	{
	    Object cachedData = requestor.get(RECOVERY_TIMEOUT, TimeUnit.SECONDS);
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    X509CRL crl = (X509CRL) cf.generateCRL(new ByteArrayInputStream((byte[]) cachedData));
	    log.debug("Time for recovering a CRL from memcached: " + ((System.currentTimeMillis() - startTime) / 1000));
	    return (crl);
	}
	catch (Exception exc)
	{
	    return (null);
	}
    }

    private void setData(Object data)
    {
	if (data == null)
	{
	    memcachedClient.delete(CRL_LABEL);
	}
	else
	{
	    memcachedClient.set(CRL_LABEL, 60 * 60 * 24 * 30, data);
	}
    }
}
