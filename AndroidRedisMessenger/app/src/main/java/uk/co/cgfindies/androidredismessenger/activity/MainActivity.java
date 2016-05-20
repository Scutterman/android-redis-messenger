package uk.co.cgfindies.androidredismessenger.activity;

import android.os.Bundle;

import org.droidparts.activity.support.v7.AppCompatActivity;

import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.async.RandomMessageRunnable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RandomMessageRunnable.getInstance(this);
    }
}
