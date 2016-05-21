package uk.co.cgfindies.androidredismessenger.activity;

import android.os.Bundle;

import org.droidparts.activity.support.v7.AppCompatActivity;
import org.droidparts.util.L;

import redis.embedded.RedisServer;
import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.async.GetMessageRunnable;
import uk.co.cgfindies.androidredismessenger.async.RandomMessageRunnable;

public class MainActivity extends AppCompatActivity implements GetMessageRunnable.NewMessageFoundInListInterface
{
    private RedisServer redisServer = null;
    private RandomMessageRunnable rmr = null;
    private GetMessageRunnable gmr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rmr = RandomMessageRunnable.getInstance(this);
        gmr = GetMessageRunnable.getInstance(this);

        new Thread(rmr).start();
        new Thread(gmr).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rmr != null)
        {
            rmr.close();
        }

        if (gmr != null)
        {
            gmr.close();
        }
    }

    @Override
    public void newMessageFound(String message) {
        L.w("Get Message: " + message);
    }
}
