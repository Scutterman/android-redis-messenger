package uk.co.cgfindies.androidredismessenger.storage;

import redis.clients.jedis.Jedis;

/**
 * Provides a Jedis instance.
 */
public class JedisProvider
{
    private static JedisProvider instance;
    private Jedis jedis;

    private JedisProvider() {}

    public Jedis getJedisInstance()
    {
        if (jedis != null && jedis.isConnected())
        {
            return jedis;
        }

        return jedis = new Jedis("192.168.1.112");
    }

    public static void close()
    {
        if (getInstance().jedis != null)
        {
            getInstance().jedis.close();
        }
    }

    public static JedisProvider getInstance()
    {
        if (instance == null)
        {
            instance = new JedisProvider();
        }

        return instance;
    }
}
