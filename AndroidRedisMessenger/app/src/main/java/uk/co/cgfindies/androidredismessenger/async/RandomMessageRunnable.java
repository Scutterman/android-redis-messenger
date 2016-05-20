package uk.co.cgfindies.androidredismessenger.async;

import android.content.Context;

import org.droidparts.util.L;

import java.util.Random;

import uk.co.cgfindies.androidredismessenger.R;

/**
 * Provides a random message once every 20 seconds on average.
 */
public class RandomMessageRunnable extends RepeatRunnable
{

    private static RandomMessageRunnable instance = null;

    private int maxRandom = 1000000;
    private int percentChance = 20;
    private int hitMax = (int)Math.ceil(maxRandom * percentChance * 0.01);
    private int stringArrayLength = 0;
    private int arrayHitMax;

    private String[] values;

    /**
     * Set up the runner to get a random string every 20 seconds on average.
     *
     * @param ctx Used to get strings.
     */
    private RandomMessageRunnable(Context ctx)
    {
        super();
        values = ctx.getResources().getStringArray(R.array.random_messages);
        stringArrayLength = values.length;
        arrayHitMax = (int)Math.round(hitMax / stringArrayLength);
    }

    @Override
    public void run()
    {
        Random random = new Random(System.currentTimeMillis());
        int hit = random.nextInt(maxRandom);

        if (hit <= hitMax)
        {
            int arrayIndex = (int)Math.round(hit / arrayHitMax);
            try
            {
                L.w(values[arrayIndex]);
            }
            catch (ArrayIndexOutOfBoundsException e) {}
        }

        super.run();
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
}
