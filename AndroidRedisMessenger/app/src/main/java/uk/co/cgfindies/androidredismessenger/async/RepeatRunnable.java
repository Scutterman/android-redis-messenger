package uk.co.cgfindies.androidredismessenger.async;

import android.os.Handler;

import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a runnable that re-runs itself.
 */
public class RepeatRunnable implements Runnable
{
    Handler handler = new Handler();
    public long delay = 1000;
    protected boolean runNextTime = true;
    protected JedisProvider.HandleNoConnectionInterface hncInterface;

    public RepeatRunnable(JedisProvider.HandleNoConnectionInterface hncInterface)
    {
        this.hncInterface = hncInterface;
    }

    @Override
    public void run()
    {
        if (!runNextTime)
        {
            return;
        }

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
        runNextTime = false;
    }
}
