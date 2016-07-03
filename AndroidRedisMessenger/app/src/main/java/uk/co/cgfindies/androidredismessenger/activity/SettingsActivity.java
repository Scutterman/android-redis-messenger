package uk.co.cgfindies.androidredismessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.droidparts.activity.support.v7.AppCompatActivity;
import org.droidparts.util.L;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.fragment.SettingsFragment;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

public class SettingsActivity extends AppCompatActivity
{

    public static Intent getIntent(Context ctx) {
        return new Intent(ctx, SettingsActivity.class);
    }
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        settingsFragment = new SettingsFragment();

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            settingsFragment.setArguments(extras);
        }

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, settingsFragment)
            .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                settingsFragment.setErrorText("Testing Connection...");
                L.w("Testing Connection...");
                new Thread(new TestJedisConnectionSettings()).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class TestJedisConnectionSettings implements Runnable, JedisProvider.DoThisInterface, JedisProvider.HandleNoConnectionInterface
    {

        @Override
        public void doThis(Jedis jedis)
        {
            L.w("Connection after test.");
            finish();
        }

        @Override
        public void handleNoConnection()
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    settingsFragment.setErrorText("Jedis connection might not be available, please check host and port.");
                    L.w("No Connection after test.");
                }
            });
        }

        @Override
        public void run()
        {
            JedisProvider.doThis(this, this);
        }
    }
}
