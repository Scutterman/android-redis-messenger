package uk.co.cgfindies.androidredismessenger.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.droidparts.activity.ListActivity;
import org.droidparts.adapter.widget.ArrayAdapter;
import org.droidparts.annotation.inject.InjectView;

import java.util.ArrayList;

import uk.co.cgfindies.androidredismessenger.R;
import uk.co.cgfindies.androidredismessenger.async.GetMessageRunnable;
import uk.co.cgfindies.androidredismessenger.async.RandomMessageRunnable;

public class MainActivity extends ListActivity implements GetMessageRunnable.NewMessageFoundInListInterface
{
    private RandomMessageRunnable rmr = null;
    private GetMessageRunnable gmr = null;

    private MessageAdapter adapter;

    @InjectView(id=android.R.id.list)
    private ListView list;

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

        rmr = RandomMessageRunnable.getInstance(this);
        gmr = GetMessageRunnable.getInstance(this);

        new Thread(rmr).start();
        new Thread(gmr).start();

        adapter.add("abcdef ghijkl mnopqrstuv wxyz abcdef ghijkl mnopqrstuv wxyz abcdef ghijkl mnopqrstuv wxyz abcdef ghijkl mnopqrstuv wxyz abcdef ghijkl mnopqrstuv wxyz ");
        adapter.add("a");
        adapter.notifyDataSetInvalidated();
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
    public void newMessageFound(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.add(message);
                adapter.notifyDataSetInvalidated();
                list.setSelection(adapter.getCount()-1);
            }
        });
    }

    private class MessageAdapter extends ArrayAdapter<String>
    {

        public MessageAdapter(Context ctx) {
            super(ctx, R.layout.list_item, new ArrayList<String>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            } else {
                view = convertView;
            }

            String message = getItem(position);
            ((TextView)view.findViewById(android.R.id.text1)).setText(getString(R.string.username));
            ((TextView)view.findViewById(android.R.id.text1)).setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.ic_menu_agenda, 0, 0);
            ((TextView)view.findViewById(android.R.id.text2)).setText(message);

            return view;
        }
    }
}
