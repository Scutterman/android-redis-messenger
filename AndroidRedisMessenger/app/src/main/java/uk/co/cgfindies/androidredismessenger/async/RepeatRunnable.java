package uk.co.cgfindies.androidredismessenger.async;

import android.os.Handler;

import org.droidparts.util.L;

import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a runnable that re-runs itself.
 */
class RepeatRunnable implements Runnable
{
    Handler handler = new Handler();
    public long delay = 1000;

    @Override
    public void run()
    {
        final RepeatRunnable runnable = this;
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                new Thread(runnable).start();
            }
        }, delay);
    }

    public void close()
    {
        JedisProvider.close();
    }
}
