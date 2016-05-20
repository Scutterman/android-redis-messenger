package uk.co.cgfindies.androidredismessenger.async;

import android.os.Handler;

/**
 * Provides a base class for a Runnable that runs repeatedly.
 */
abstract class RepeatRunnable implements Runnable
{
    // The Handler that does the running.
    protected final Handler handler = new Handler();

    // The delay. Change in the child class constructor.
    protected long delay = 1000;

    // Set up the hanler to post for the first time
    public RepeatRunnable()
    {
        handler.postDelayed(this, delay);
    }

    // Repeat the handler post
    @Override
    public void run()
    {
        handler.postDelayed(this, delay);
    }
}
