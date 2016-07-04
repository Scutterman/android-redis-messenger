package uk.co.cgfindies.androidredismessenger.application;

import android.app.Application;

import uk.co.cgfindies.androidredismessenger.fragment.SettingsFragment;

public class BaseApplication extends Application
{
    public static int jedisPort = SettingsFragment.SETTING_HOST_PORT_VALUE_DEFAULT_INT;
    public static String jedisHost = "";
}
