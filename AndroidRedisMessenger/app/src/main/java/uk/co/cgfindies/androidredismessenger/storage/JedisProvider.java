package uk.co.cgfindies.androidredismessenger.storage;

import org.droidparts.util.L;

import redis.clients.jedis.Jedis;

/**
 * Provides a Jedis instance.
 */
public class JedisProvider
{
    public interface DoThisInterface
    {
        void doThis(Jedis jedis);
    }

    private static JedisProvider instance;
    private Jedis jedis;
    private int instancesProvided = 0;

    private JedisProvider() {}

    public Jedis getJedisInstance()
    {
        if (jedis == null || !jedis.isConnected())
        {
            instancesProvided = 0;
            jedis = new Jedis("192.168.1.112");
        }

        instancesProvided++;
        return jedis;
    }

    public void closeIfLastInstance()
    {
        if (jedis == null)
        {
            instancesProvided = 0;
        }
        else if (instancesProvided == 1)
        {
            jedis.close();
            jedis = null;
            instancesProvided = 0;
        }
        else if (instancesProvided < 1)
        {
            instancesProvided = 0;
            jedis.close();
            jedis = null;
        }
        else if (instancesProvided > 1)
        {
            instancesProvided--;
        }
    }

    public static void close()
    {
        if (getInstance().jedis != null)
        {
            getInstance().closeIfLastInstance();
        }
    }

    protected static JedisProvider getInstance()
    {
        return new JedisProvider();
    }

    public static void doThis(DoThisInterface iface)
    {
        if (iface == null)
        {
            return;
        }

        Jedis jedis = null;

        try
        {
            jedis = JedisProvider.getInstance().getJedisInstance();
            iface.doThis(jedis);
        }
        catch (Exception e)
        {
            L.w(e);
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }

    }

}
