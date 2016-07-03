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
import uk.co.cgfindies.androidredismessenger.application.BaseApplication;

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
    public static final int SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT_INT = 6379;

    public static final String ARGUMENT_UNSUCCESSFUL_CONNECTION = "ARGUMENT_UNSUCCESSFUL_CONNECTION";

    private boolean unsuccessfulConnection = false;
    private TextView errorBox;

    SharedPreferences prefs;
    SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SETTING_GENERATE_MESSAGES_KEY) || !sharedPreferences.contains((key))) {
                return;
            }

            String value = sharedPreferences.getString(key, "");

            if (key.equals(SETTING_IP_ADDRESS_PORT_KEY))
            {
                if (value.length() == 0)
                {
                    value = SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SETTING_IP_ADDRESS_PORT_KEY, value);
                    editor.apply();
                }

                int portIntValue = SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT_INT;

                try {
                    portIntValue = Integer.parseInt(value);
                    if (portIntValue < 0)
                    {
                        portIntValue = SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT_INT;
                    }
                }
                catch (NumberFormatException ignored) {}

                BaseApplication.jedisPort = portIntValue;
            }

            if (key.equals(SETTING_IP_ADDRESS_KEY))
            {
                BaseApplication.jedisHost = value;
            }

            Preference pref = findPreference((key));
            pref.setSummary(value);
        }
    };

    public SettingsFragment() { }

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

        if (getArguments() != null && getArguments().containsKey(ARGUMENT_UNSUCCESSFUL_CONNECTION))
        {
            this.unsuccessfulConnection = getArguments().getBoolean(ARGUMENT_UNSUCCESSFUL_CONNECTION);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorBox = (TextView)getActivity().findViewById(R.id.settings_fragment_errors);

        List<String> errors = validateSettings(getActivity());

        if (unsuccessfulConnection)
        {
            errors.add("Jedis connection might not be available, please check host and port.");
        }

        String allErrors = "";
        for (String error : errors)
        {
            allErrors += error + "\n";
        }

        setErrorText(allErrors);
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
        else
        {
            BaseApplication.jedisHost = ipAddress;
        }

        try {
            int portAsNumber = Integer.parseInt(port);
            if (portAsNumber < 0)
            {
                // Negative port number?
                errors.add("Port number must not be negative.");
            }
            else
            {
                BaseApplication.jedisPort = portAsNumber;
            }
        }
        catch (NumberFormatException ex)
        {
            errors.add("Port must be a number.");
        }

        return errors;
    }

    public void setErrorText(String message)
    {
        if (errorBox != null)
        {
            errorBox.setText(message);
        }
    }
}
