package uk.co.cgfindies.androidredismessenger.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.droidparts.activity.ListActivity;
import org.droidparts.adapter.widget.ArrayAdapter;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.annotation.inject.InjectView;

import java.util.ArrayList;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.application.PrefsManager;
import uk.co.cgfindies.androidredismessenger.async.GetMessageRunnable;
import uk.co.cgfindies.androidredismessenger.async.RandomMessageRunnable;
import uk.co.cgfindies.androidredismessenger.async.UserRunnable;
import uk.co.cgfindies.androidredismessenger.model.MessageDetails;
import uk.co.cgfindies.androidredismessenger.model.User;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

public class MainActivity extends ListActivity implements GetMessageRunnable.NewMessageFoundInListInterface
{
    private RandomMessageRunnable rmr = null;
    private GetMessageRunnable gmr = null;

    private MessageAdapter adapter;

    @InjectView(id=android.R.id.list)
    private ListView list;

    @InjectDependency
    private PrefsManager prefs;

    @Override
    protected void onPreInject() {
        super.onPreInject();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new MessageAdapter(this);
        setListAdapter(adapter);

        setupMessageProcesses();
        createUserIfNotExists();
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
    public void newMessageFound(final MessageDetails messageDetails)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.add(messageDetails);
                adapter.notifyDataSetInvalidated();
                list.setSelection(adapter.getCount()-1);
            }
        });
    }

    private void setupMessageProcesses()
    {
        new Thread(new UserRunnable()).start();

        rmr = RandomMessageRunnable.getInstance(this);
        gmr = GetMessageRunnable.getInstance(this);

        new Thread(rmr).start();
        new Thread(gmr).start();
    }

    private void createUserIfNotExists()
    {
        if (prefs.get("username").length() > 0)
        {
            return;
        }

        class CreateUserRunnable implements Runnable, JedisProvider.DoThisInterface {
            @Override
            public void run() {
                JedisProvider.doThis(this);
            }

            @Override
            public void doThis(Jedis jedis) {
                String username = User.addUser(jedis, -1);
                prefs.set("username", username);
            }
        }
        new Thread(new CreateUserRunnable()).start();
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
            String userColour = messageDetails.getUser().get("colour");

            TextView text1 = ((TextView)view.findViewById(android.R.id.text1));
            text1.setText(messageDetails.get("username"));
            Drawable drawable = getDrawable(android.R.drawable.ic_menu_agenda);

            if (userColour != null && userColour.length() > 0 && drawable != null)
            {
                drawable.mutate().setColorFilter(Color.parseColor(userColour), PorterDuff.Mode.SRC_IN);
            }

            text1.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

            ((TextView)view.findViewById(android.R.id.text2)).setText(messageDetails.get("message"));

            return view;
        }
    }
}
