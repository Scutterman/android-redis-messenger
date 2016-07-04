package uk.co.cgfindies.androidredismessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.droidparts.activity.support.v7.AppCompatActivity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import uk.co.cgfindies.androidredismessenger.R;
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
                settingsFragment.setErrorText(R.string.setting_fragment_testing_connection);
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
            try
            {
                jedis.ping();
                // If ping doesn't cause a connection exception, we can assume the connection is good.
                // Technically, we don't need to do all this try/catch/handle stuff here, because it's done in JedisProvider.doThis
                // But this is an important piece of code so legibility and understandability matter.
                finish();
            }
            catch (JedisConnectionException ex)
            {
                handleNoConnection();
            }
            finally
            {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }

        @Override
        public void handleNoConnection()
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    settingsFragment.setErrorText(R.string.setting_fragment_connection_unavailable);
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
