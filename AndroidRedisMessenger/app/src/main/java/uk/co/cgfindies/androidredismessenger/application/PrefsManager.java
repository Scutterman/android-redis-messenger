package uk.co.cgfindies.androidredismessenger.application;

import android.content.Context;

import org.droidparts.persist.AbstractPrefsManager;

/**
 * Provides a way of managing dependencies
 */
public class PrefsManager extends AbstractPrefsManager
{
    public PrefsManager(Context ctx)
    {
        super(ctx, 1);
    }

    public boolean isUserCreated()
    {
        return (getPreferences().contains("username"));
    }

    public void set(String key, String value)
    {
        saveString(key, value);
    }

    public String get(String key)
    {
        return getPreferences().getString(key, "");
    }
}
