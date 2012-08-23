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
import org.orinocoX509.entity.CertificateProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class CacheableCertificateProfileMemcachedAspect  
{
	@Autowired
	private MemcachedClient memcachedClient;
	private final String ENTRY_PREFIX = "orinocoX509_certificate_profile_";
	private final int RECOVERY_TIMEOUT = 2;
	
	private static final Logger log = LoggerFactory.getLogger(CacheableCertificateProfileMemcachedAspect.class);
	
	@Pointcut("execution(* org.orinocoX509.service.CertificateProfileService.getProfile(..))")
	public void cacheableCertificateProfilePointcutExpression()  {}
	
	@Around("cacheableCertificateProfilePointcutExpression()")
	public Object loadCacheAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable     
	{
		Object cachedData = getCachedData(proceedingJoinPoint);
		if (cachedData != null)
		{
			return(cachedData);
		}
		
		CertificateProfile  certificateProfile = (CertificateProfile) proceedingJoinPoint.proceed();
		setData(certificateProfile);
		return(certificateProfile);
	}
	
	@Pointcut("execution(* org.orinocoX509.service.CertificateProfileService.saveProfile(..)) || " +
			"execution(* org.orinocoX509.service.CertificateProfileService.deleteProfile(..)) ")
	public void cacheEvictCertificateProfilePointcutExpression()  {}
	
	@After("cacheEvictCertificateProfilePointcutExpression()")
	public void evictCacheAspect(JoinPoint joinPoint) throws Throwable     
	{
		CertificateProfile certificateProfile = (CertificateProfile) joinPoint.getArgs()[0]; 
		clearData(certificateProfile);
	}
	
	private Object getCachedData(ProceedingJoinPoint proceedingJoinPoint)
	{
		long startTime = System.currentTimeMillis();
	 	CertificateProfile certificateProfile = (CertificateProfile) proceedingJoinPoint.getArgs()[0];
		Future<Object> requestor = memcachedClient.asyncGet(ENTRY_PREFIX + certificateProfile.getProfileId());
		
		try
		{
			Object cachedData = requestor.get(RECOVERY_TIMEOUT,TimeUnit.SECONDS);
			log.debug("Time for recovering a certificate profile from memcached: " +  ((System.currentTimeMillis()-startTime) / 1000));
			return(cachedData);
		}
		catch(Exception exc) 
		{
			return(null);
		}
	}
	
	private void setData(CertificateProfile certificateProfile)
	{
		memcachedClient.set(ENTRY_PREFIX + certificateProfile.getProfileId(), 60*60*24*30, certificateProfile);
	}
	
	private void clearData(CertificateProfile certificateProfile)
	{
		memcachedClient.delete(ENTRY_PREFIX + certificateProfile.getProfileId());
	}
}
