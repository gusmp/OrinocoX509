package org.orinocoX509.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MemCachedCacheTest extends BaseDistributedCacheTest
{
    // Enable this test only if you Memcached is up and running!

    @Before
    public void init()
    {
    }

    @Test
    public void emptyTest()
    {
    }

}
