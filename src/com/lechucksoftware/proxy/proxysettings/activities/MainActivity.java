package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.*;


/**
 * Created by marco on 17/05/13.
 */
public class MainActivity extends Activity
{
    private MainAPPrefsFragment mainFragment;
    private HelpPrefsFragment helpFragment;
    private ProxyCheckerPrefsFragment checkFragment;
    private AdvancedPrefsFragment advFragment;
    private APSelectorFragment apSelectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        mainFragment = new MainAPPrefsFragment();
        checkFragment = new ProxyCheckerPrefsFragment();
        advFragment = new AdvancedPrefsFragment();
        helpFragment = new HelpPrefsFragment();
        apSelectorFragment = new APSelectorFragment();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null)
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                return;
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.fragment_container,apSelectorFragment) .commit();
        }


    }
}
