package uk.co.cgfindies.androidredismessenger.async;

import android.content.Context;

import java.util.Random;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.model.MessageDetails;
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
    private RandomMessageRunnable(Context ctx, JedisProvider.HandleNoConnectionInterface hncInterface)
    {
        super(hncInterface);
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
    public static RandomMessageRunnable getInstance(Context ctx, JedisProvider.HandleNoConnectionInterface hncInterface)
    {
        if (instance == null)
        {
            instance = new RandomMessageRunnable(ctx, hncInterface);
        }

        instance.runNextTime = true;
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
            JedisProvider.doThis(this, hncInterface);
        }

        super.run();
    }

    @Override
    public void doThis(Jedis jedis)
    {
        String username = jedis.srandmember("usernames");
        MessageDetails.addMessage(jedis, messageContent, username, messageTime);
    }
}
