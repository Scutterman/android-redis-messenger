package uk.co.cgfindies.androidredismessenger.application;

import android.app.Application;

import uk.co.cgfindies.androidredismessenger.fragment.SettingsFragment;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Created by Scutterman on 21/05/2016.
 */
public class BaseApplication extends Application
{
    public static int jedisPort = SettingsFragment.SETTING_IP_ADDRESS_PORT_VALUE_DEFAULT_INT;
    public static String jedisHost = "";
}
