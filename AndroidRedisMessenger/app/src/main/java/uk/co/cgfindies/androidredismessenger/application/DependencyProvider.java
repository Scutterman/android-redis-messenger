package uk.co.cgfindies.androidredismessenger.application;

import android.content.Context;

import org.droidparts.AbstractDependencyProvider;

/**
 * Provides a way to inject dependencies
 */
public class DependencyProvider extends AbstractDependencyProvider
{
    private PrefsManager prefs;
    public DependencyProvider(Context ctx)
    {
        super(ctx);
    }

    public PrefsManager getPrefsManager()
    {
        if (prefs == null)
        {
            prefs = new PrefsManager(getContext());
        }

        return prefs;
    }
}
