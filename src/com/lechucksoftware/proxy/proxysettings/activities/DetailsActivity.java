package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.fragments.MainAPPrefsFragment;

/**
 * Created by marco on 17/05/13.
 */
public class DetailsActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null)
        {
            // During initial setup, plug in the details fragment.
            MainAPPrefsFragment details = new MainAPPrefsFragment();
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}