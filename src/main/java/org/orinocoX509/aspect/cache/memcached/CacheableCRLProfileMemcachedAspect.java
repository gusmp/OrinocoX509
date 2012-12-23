package org.orinocoX509.aspect.cache.memcached;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.spy.memcached.MemcachedClient;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.orinocoX509.entity.CRLProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class CacheableCRLProfileMemcachedAspect
{
    @Autowired
    private MemcachedClient memcachedClient;
    private final String ENTRY_PREFIX = "orinocoX509_crl_profile_";
    private final int RECOVERY_TIMEOUT = 2;

    private static final Logger log = LoggerFactory.getLogger(CacheableCRLProfileMemcachedAspect.class);

    @Pointcut("execution(* org.orinocoX509.service.CRLProfileService.getProfile(..))")
    public void cacheableCRLProfilePointcutExpression()
    {
    }

    @Around("cacheableCRLProfilePointcutExpression()")
    public Object loadCacheAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
	Object cachedData = getCachedData(proceedingJoinPoint);
	if (cachedData != null)
	{
	    return (cachedData);
	}

	CRLProfile crltProfile = (CRLProfile) proceedingJoinPoint.proceed();
	setData(crltProfile);
	return (crltProfile);
    }

    @Pointcut("execution(* org.orinocoX509.service.CRLProfileService.saveProfile(..)) || " + "execution(* org.orinocoX509.service.CRLProfileService.deleteProfile(..)) ")
    public void cacheEvictCRLProfilePointcutExpression()
    {
    }

    @After("cacheEvictCRLProfilePointcutExpression()")
    public void evictCacheAspect(JoinPoint joinPoint) throws Throwable
    {
	CRLProfile crlProfile = (CRLProfile) joinPoint.getArgs()[0];
	clearData(crlProfile);
    }

    private Object getCachedData(ProceedingJoinPoint proceedingJoinPoint)
    {
	long startTime = System.currentTimeMillis();
	CRLProfile crlProfile = (CRLProfile) proceedingJoinPoint.getArgs()[0];
	Future<Object> requestor = memcachedClient.asyncGet(ENTRY_PREFIX + crlProfile.getProfileId());

	try
	{
	    Object cachedData = requestor.get(RECOVERY_TIMEOUT, TimeUnit.SECONDS);
	    log.debug("Time for recovering a CRL profile from memcached: " + ((System.currentTimeMillis() - startTime) / 1000));
	    return (cachedData);
	}
	catch (Exception exc)
	{
	    return (null);
	}
    }

    private void setData(CRLProfile crlProfile)
    {
	memcachedClient.set(ENTRY_PREFIX + crlProfile.getProfileId(), 60 * 60 * 24 * 30, crlProfile);
    }

    private void clearData(CRLProfile crlProfile)
    {
	memcachedClient.delete(ENTRY_PREFIX + crlProfile.getProfileId());
    }
}
