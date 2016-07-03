package uk.co.cgfindies.androidredismessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.droidparts.activity.support.v7.AppCompatActivity;
import uk.co.cgfindies.androidredismessenger.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity
{

    public static Intent getIntent(Context ctx) {
        return new Intent(ctx, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
