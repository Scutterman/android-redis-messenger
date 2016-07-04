package uk.co.cgfindies.androidredismessenger.storage;

import org.droidparts.util.L;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import uk.co.cgfindies.androidredismessenger.application.BaseApplication;

/**
 * Provides a Jedis instance.
 */
public class JedisProvider
{
    public interface DoThisInterface
    {
        void doThis(Jedis jedis);
    }

    public interface HandleNoConnectionInterface
    {
        void handleNoConnection();
    }

    private Jedis jedis;
    private int instancesProvided = 0;

    private JedisProvider() {}

    public Jedis getJedisInstance()
    {
        if (jedis == null || !jedis.isConnected())
        {
            instancesProvided = 0;
            jedis = new Jedis(BaseApplication.jedisHost, BaseApplication.jedisPort);
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

    public static void doThis(DoThisInterface iface, HandleNoConnectionInterface hncInterface)
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
        catch (JedisConnectionException e)
        {
            if (hncInterface != null)
            {
                hncInterface.handleNoConnection();
            }

        }
        catch (Exception ex)
        {
            L.w(ex);
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
