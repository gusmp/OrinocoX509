package org.orinocoX509.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RedisCacheTest extends BaseDistributedCacheTest
{

    // Enable this test only if you Redis server is up and running!

    @Before
    public void init()
    {
    }

    @Test
    public void emptyTest()
    {
    }

    @Override
    @Test
    public void testCRLCache()
    {
	// No supported by Redis
	// RedisTemplate cannot serialize the object
	// sun.security.x509.X509CRLImpl using default Serializer
	// (JdkSerializationRedisSerializer)
    }

    @Override
    @Test
    public void testCertificateProfileCache()
    {
    }

    @Override
    @Test
    public void testCRLProfileCache()
    {
    }

}
