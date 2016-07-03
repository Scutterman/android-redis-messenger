package uk.co.cgfindies.androidredismessenger.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.cgfindies.androidredismessenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
{
    public static final String SETTING_IP_ADDRESS_KEY = "SETTING_IP_ADDRESS";
    public static final String SETTING_IP_ADDRESS_PORT_KEY = "SETTING_IP_ADDRESS_PORT";
    public static final String SETTING_GENERATE_MESSAGES_KEY = "SETTING_GENERATE_MESSAGES";
    public static final String SETTING_USERNAME = "SETTING_USERNAME";

    public static final String SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT = "6379";

    SharedPreferences prefs;
    SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SETTING_GENERATE_MESSAGES_KEY) || !sharedPreferences.contains((key))) {
                return;
            }

            String value = sharedPreferences.getString(key, "");

            if (key.equals(SETTING_IP_ADDRESS_PORT_KEY) && value.length() == 0)
            {
                value = SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SETTING_IP_ADDRESS_PORT_KEY, value);
                editor.apply();
            }

            Preference pref = findPreference((key));
            pref.setSummary(value);
        }
    };

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(mListener);

        mListener.onSharedPreferenceChanged(prefs, SETTING_IP_ADDRESS_KEY);
        mListener.onSharedPreferenceChanged(prefs, SETTING_IP_ADDRESS_PORT_KEY);
        mListener.onSharedPreferenceChanged(prefs, SETTING_GENERATE_MESSAGES_KEY);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String allErrors = "";
        for (String error : validateSettings(getActivity()))
        {
            allErrors += error + "\n";
        }

        if (allErrors.length() > 0)
        {
            TextView errorBox = (TextView)getActivity().findViewById(R.id.settings_fragment_errors);
            if (errorBox != null)
            {
                errorBox.setText(allErrors);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onDestroy()
    {
        prefs.unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }

    public static List<String> validateSettings(Context ctx)
    {
        List<String> errors = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String ipAddress = sharedPreferences.getString(SettingsFragment.SETTING_IP_ADDRESS_KEY, "");
        String port = sharedPreferences.getString(SettingsFragment.SETTING_IP_ADDRESS_PORT_KEY, SettingsFragment.SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT);

        if (port.length() == 0)
        {
            port = SettingsFragment.SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT;
        }

        if (ipAddress.length() == 0)
        {
            errors.add("IP Address must be supplied and not empty.");
        }

        try {
            int portAsNumber = Integer.parseInt(port);
            if (portAsNumber < 0)
            {
                // Negative port number?
                errors.add("Port number must not be negative.");
            }
        }
        catch (NumberFormatException ex)
        {
            errors.add("Port must be a number.");
        }

        return errors;
    }

}
