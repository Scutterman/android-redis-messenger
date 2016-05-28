package uk.co.cgfindies.androidredismessenger.async;

import android.content.Context;

import org.droidparts.util.L;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a random message once every 20 seconds on average.
 */
public class RandomMessageRunnable extends RepeatRunnable
{

    private static RandomMessageRunnable instance = null;

    private int maxRandom = 1000000;
    private int percentChance = 5;
    private int hitMax = (int)Math.ceil(maxRandom * percentChance * 0.01);
    private int arrayHitMax;

    private String[] values;

    /**
     * Set up the runner to get a random string every 20 seconds on average.
     *
     * @param ctx Used to get strings.
     */
    private RandomMessageRunnable(Context ctx)
    {
        values = ctx.getResources().getStringArray(R.array.random_messages);
        int stringArrayLength = values.length;
        arrayHitMax = Math.round(hitMax / stringArrayLength);
    }

    /**
     * Don't want more than one of these running at once.
     *
     * @param ctx Used to get the strings
     * @return static
     */
    public static RandomMessageRunnable getInstance(Context ctx)
    {
        if (instance == null)
        {
            instance = new RandomMessageRunnable(ctx);
        }

        return instance;
    }

    /**
     * Set a random message at random intervals
     */
    @Override
    public void run()
    {
        long messageTime = System.currentTimeMillis();
        Random random = new Random(messageTime);
        int hit = random.nextInt(maxRandom);

        if (hit <= hitMax)
        {
            Jedis jedis = null;

            try
            {
                jedis = JedisProvider.getInstance().getJedisInstance();

                int arrayIndex = Math.round(hit / arrayHitMax);
                try
                {
                    String username = jedis.srandmember("usernames");

                    Map<String, String> message = new HashMap<>();
                    message.put("message", values[arrayIndex]);
                    message.put("timestamp", Long.toString(messageTime));
                    message.put("username", username);

                    jedis.hmset("messages:" + Long.toString(messageTime), message);
                    jedis.rpush("messageKeys", Long.toString(messageTime));
                }
                catch (JedisConnectionException e)
                {
                    L.w(e);
                }
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                L.w("The random hit could not calculate an index for the string. Number generated was "
                        + Integer.toString(hit)
                        + " with an arrayHitMax of "
                        + Integer.toString(arrayHitMax));
            }
            finally
            {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }

        super.run();
    }
}
