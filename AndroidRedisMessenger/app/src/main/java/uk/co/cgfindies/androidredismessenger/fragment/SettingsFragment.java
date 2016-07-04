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
    public static final String SETTING_HOST_KEY = "SETTING_HOST";
    public static final String SETTING_HOST_PORT_KEY = "SETTING_HOST_PORT";
    public static final String SETTING_GENERATE_MESSAGES_KEY = "SETTING_GENERATE_MESSAGES";
    public static final String SETTING_USERNAME = "SETTING_USERNAME";

    public static final String SETTING_HOST_PORT_VALUE_DEFAULT = "6379";
    public static final int SETTING_HOST_PORT_VALUE_DEFAULT_INT = 6379;

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

            if (key.equals(SETTING_HOST_PORT_KEY))
            {
                if (value.length() == 0)
                {
                    value = SETTING_HOST_PORT_VALUE_DEFAULT;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SETTING_HOST_PORT_KEY, value);
                    editor.apply();
                }

                int portIntValue = SETTING_HOST_PORT_VALUE_DEFAULT_INT;

                try {
                    portIntValue = Integer.parseInt(value);
                    if (portIntValue < 0)
                    {
                        portIntValue = SETTING_HOST_PORT_VALUE_DEFAULT_INT;
                    }
                }
                catch (NumberFormatException ignored) {}

                BaseApplication.jedisPort = portIntValue;
            }

            if (key.equals(SETTING_HOST_KEY))
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

        mListener.onSharedPreferenceChanged(prefs, SETTING_HOST_KEY);
        mListener.onSharedPreferenceChanged(prefs, SETTING_HOST_PORT_KEY);
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
            errors.add(getString(R.string.setting_fragment_connection_unavailable));
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
        String host = sharedPreferences.getString(SettingsFragment.SETTING_HOST_KEY, "");
        String port = sharedPreferences.getString(SettingsFragment.SETTING_HOST_PORT_KEY, SettingsFragment.SETTING_HOST_PORT_VALUE_DEFAULT);

        if (port.length() == 0)
        {
            port = SettingsFragment.SETTING_HOST_PORT_VALUE_DEFAULT;
        }

        if (host.length() == 0)
        {
            errors.add(ctx.getString(R.string.setting_fragment_host_must_be_present));
        }
        else
        {
            BaseApplication.jedisHost = host;
        }

        try {
            int portAsNumber = Integer.parseInt(port);
            if (portAsNumber < 0)
            {
                // Negative port number?
                errors.add(ctx.getString(R.string.setting_fragment_negative_port));
            }
            else
            {
                BaseApplication.jedisPort = portAsNumber;
            }
        }
        catch (NumberFormatException ex)
        {
            errors.add(ctx.getString(R.string.setting_fragment_port_not_a_number));
        }

        return errors;
    }

    public void setErrorText(int stringResourceId)
    {
        if (errorBox != null)
        {
            errorBox.setText(stringResourceId);
        }
    }

    public void setErrorText(String message)
    {
        if (errorBox != null)
        {
            errorBox.setText(message);
        }
    }
}
