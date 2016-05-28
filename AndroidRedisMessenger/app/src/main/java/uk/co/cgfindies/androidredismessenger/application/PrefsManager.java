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
}
