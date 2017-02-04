import org.junit.Assert;
import sun.rmi.rmic.Constants;
import org.junit.Test;

import static sun.rmi.rmic.Constants.SIG_INT;
import static sun.tools.java.RuntimeConstants.SIG_BOOLEAN;

/**
 * Created by abhinav.jaiswal on 04/02/17.
 */

public class CacheTest {

    @Test
    public void testIfGetBeforeExpiry() throws InterruptedException {
        CacheService<String, String> cacheService = new CacheServiceImpl<String, String>();
        cacheService.put(SIG_INT, SIG_BOOLEAN, 1000);

        Thread.sleep(900);

        Assert.assertEquals(SIG_BOOLEAN, cacheService.get(SIG_INT));
    }

    @Test
    public void testIfGetAfterExpiry() throws InterruptedException {
        CacheService<String, String> cacheService = new CacheServiceImpl<String, String>();
        cacheService.put(SIG_INT, SIG_BOOLEAN, 1000);

        Thread.sleep(1100);

        Assert.assertEquals(null, cacheService.get(SIG_INT));
    }
}
