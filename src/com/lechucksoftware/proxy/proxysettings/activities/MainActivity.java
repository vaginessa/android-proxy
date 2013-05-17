package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.*;


/**
 * Created by marco on 17/05/13.
 */
public class MainActivity extends Activity
{
    private ProxyDetailsFragment mainFragment;
    private HelpPrefsFragment helpFragment;
    private ProxyCheckerPrefsFragment checkFragment;
    private AdvancedPrefsFragment advFragment;
    private AccessPointListFragment apSelectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        mainFragment = new ProxyDetailsFragment();
        checkFragment = new ProxyCheckerPrefsFragment();
        advFragment = new AdvancedPrefsFragment();
        helpFragment = new HelpPrefsFragment();
        apSelectorFragment = new AccessPointListFragment();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FragmentTransaction transaction = null;

        switch (item.getItemId())
        {
            case android.R.id.home:
                // Clean-up the backstack when going back to home
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, apSelectorFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
