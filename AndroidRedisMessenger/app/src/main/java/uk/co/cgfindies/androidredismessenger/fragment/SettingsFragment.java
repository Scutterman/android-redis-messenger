package uk.co.cgfindies.androidredismessenger.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

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

    SharedPreferences prefs;
    SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SETTING_GENERATE_MESSAGES_KEY) || !sharedPreferences.contains((key))) {
                return;
            }

            String value = sharedPreferences.getString(key, "");
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
    public void onDestroy()
    {
        prefs.unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }

}
