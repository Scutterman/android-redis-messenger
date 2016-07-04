package uk.co.cgfindies.androidredismessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.droidparts.activity.support.v7.AppCompatActivity;
import org.droidparts.adapter.widget.ArrayAdapter;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.util.ui.ViewUtils;
import org.droidparts.widget.ClearableEditText;

import java.util.ArrayList;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.async.GetMessageRunnable;
import uk.co.cgfindies.androidredismessenger.async.RandomMessageRunnable;
import uk.co.cgfindies.androidredismessenger.async.UserRunnable;
import uk.co.cgfindies.androidredismessenger.fragment.SettingsFragment;
import uk.co.cgfindies.androidredismessenger.model.MessageDetails;
import uk.co.cgfindies.androidredismessenger.model.User;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

public class MainActivity extends AppCompatActivity implements GetMessageRunnable.NewMessageFoundInListInterface, View.OnClickListener, JedisProvider.HandleNoConnectionInterface
{
    private RandomMessageRunnable rmr = null;
    private GetMessageRunnable gmr = null;

    private MessageAdapter adapter;

    @InjectView(id=android.R.id.list)
    private ListView list;

    @InjectDependency
    SharedPreferences sharedPreferences;

    @InjectView(id=R.id.add_message, click=true)
    private Button addMessageButton;
    private boolean uiAvailable;

    private boolean redirectedAfterNoConnection = false;

    @Override
    protected void onPreInject()
    {
        super.onPreInject();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        redirectedAfterNoConnection = false;
        adapter = new MessageAdapter(this);
        list.setAdapter(adapter);

        setupMessageProcessesIfRequiredSettingsArePresent();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (rmr != null)
        {
            rmr.close();
            rmr = null;
        }

        if (gmr != null)
        {
            gmr.close();
            gmr = null;
        }

        uiAvailable = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_settings:
                redirectToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == addMessageButton)
        {
            addMessage();
        }
    }

    @Override
    public void newMessageFound(final MessageDetails messageDetails)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!uiAvailable)
                {
                    return;
                }

                adapter.add(messageDetails);
                adapter.notifyDataSetInvalidated();
                list.setSelection(adapter.getCount()-1);
            }
        });
    }

    private void setupMessageProcessesIfRequiredSettingsArePresent()
    {
        if (SettingsFragment.validateSettings(this).size() > 0)
        {
            redirectToSettings();
            return;
        }
        createUserIfNotExists();

        uiAvailable = true;
        new Thread(new UserRunnable(this)).start();

        gmr = GetMessageRunnable.getInstance(this, this);
        new Thread(gmr).start();

        if (sharedPreferences.getBoolean(SettingsFragment.SETTING_GENERATE_MESSAGES_KEY, false))
        {
            rmr = RandomMessageRunnable.getInstance(this, this);
            new Thread(rmr).start();
        }

    }

    private void redirectToSettings() { redirectToSettings(false); }
    private void redirectToSettings(boolean unsuccessfulConnection)
    {
        Intent intent = SettingsActivity.getIntent(this);

        if (unsuccessfulConnection)
        {
            intent.putExtra(SettingsFragment.ARGUMENT_UNSUCCESSFUL_CONNECTION, true);
        }

        startActivity(intent);
    }

    private void createUserIfNotExists()
    {
        String username = sharedPreferences.getString(SettingsFragment.SETTING_USERNAME, "");
        if (username.length() > 0)
        {
            populateUserBox();
            return;
        }

        class CreateUserRunnable implements Runnable, JedisProvider.DoThisInterface
        {
            @Override
            public void run()
            {
                JedisProvider.doThis(this, MainActivity.this);
            }

            @Override
            public void doThis(Jedis jedis)
            {
                String username = User.addUser(jedis, -1);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SettingsFragment.SETTING_USERNAME, username);
                editor.apply();

                populateUserBox();
            }
        }
        new Thread(new CreateUserRunnable()).start();
    }

    private void populateUserBox()
    {
        class PopulateUserRunnable implements Runnable, JedisProvider.DoThisInterface
        {
            @Override
            public void run()
            {
                JedisProvider.doThis(this, MainActivity.this);
            }

            @Override
            public void doThis(Jedis jedis)
            {
                final User user = User.createFromUsername(jedis, sharedPreferences.getString(SettingsFragment.SETTING_USERNAME, ""));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!uiAvailable)
                        {
                            return;
                        }

                        setUserElements(user, R.id.username, null);
                    }
                });
            }
        }

        new Thread(new PopulateUserRunnable()).start();
    }

    private void setUserElements(User user, int viewId, View parent)
    {
        String userColour = user.get("colour");
        TextView text1;

        if (parent != null) {
            text1 = ((TextView) parent.findViewById(viewId));
        }
        else
        {
            text1 = ((TextView) findViewById(viewId));
        }

        if (text1 != null)
        {

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_agenda, null);

            if (userColour != null && userColour.length() > 0 && drawable != null)
            {
                drawable.mutate().setColorFilter(Color.parseColor(userColour), PorterDuff.Mode.SRC_IN);
            }

            text1.setText(user.get("username"));
            text1.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }


    }

    private void addMessage()
    {
        ClearableEditText messageView = (ClearableEditText) findViewById(R.id.message);
        if (messageView == null)
        {
            return;
        }

        final String message = messageView.getText().toString();
        messageView.setText("");
        ViewUtils.setKeyboardVisible(messageView, false);

        class AddMessageRunnable implements Runnable, JedisProvider.DoThisInterface
        {
            @Override
            public void run()
            {
                JedisProvider.doThis(this, MainActivity.this);
            }

            @Override
            public void doThis(Jedis jedis)
            {
                MessageDetails.addMessage(jedis, message, sharedPreferences.getString(SettingsFragment.SETTING_USERNAME, ""), -1);
            }
        }
        new Thread(new AddMessageRunnable()).start();
    }

    public void handleNoConnection()
    {
        if (!redirectedAfterNoConnection)
        {
            redirectedAfterNoConnection = true;
            redirectToSettings(true);
        }
    }

    private class MessageAdapter extends ArrayAdapter<MessageDetails>
    {

        public MessageAdapter(Context ctx) {
            super(ctx, R.layout.list_item, new ArrayList<MessageDetails>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            } else {
                view = convertView;
            }

            MessageDetails messageDetails = getItem(position);
            setUserElements(messageDetails.getUser(), android.R.id.text1, view);
            ((TextView)view.findViewById(android.R.id.text2)).setText(messageDetails.get("message"));

            return view;
        }
    }
}
