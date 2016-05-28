package uk.co.cgfindies.androidredismessenger.async;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a random message once every 20 seconds on average.
 */
public class RandomMessageRunnable extends RepeatRunnable implements JedisProvider.DoThisInterface
{

    private static RandomMessageRunnable instance = null;

    private int maxRandom = 1000000;
    private int percentChance = 5;
    private int hitMax = (int)Math.ceil(maxRandom * percentChance * 0.01);
    private int arrayHitMax;

    private String[] values;
    private long messageTime;
    private String messageContent;

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
        messageTime = System.currentTimeMillis();
        Random random = new Random(messageTime);
        int hit = random.nextInt(maxRandom);

        if (hit <= hitMax)
        {
            messageContent = values[Math.round(hit / arrayHitMax)];
            JedisProvider.doThis(this);
        }

        super.run();
    }

    @Override
    public void doThis(Jedis jedis) {
        String username = jedis.srandmember("usernames");

        Map<String, String> message = new HashMap<>();
        message.put("message", messageContent);
        message.put("timestamp", Long.toString(messageTime));
        message.put("username", username);

        jedis.hmset("messages:" + Long.toString(messageTime), message);
        jedis.rpush("messageKeys", Long.toString(messageTime));
    }
}
