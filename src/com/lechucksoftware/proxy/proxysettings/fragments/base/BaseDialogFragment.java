package com.lechucksoftware.proxy.proxysettings.fragments.base;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseActivity;
import com.shouldit.proxy.lib.log.LogWrapper;

/**
 * Created by marco on 24/05/13.
 */
public class BaseDialogFragment extends DialogFragment
{
    @Override
    public void onResume()
    {
        super.onResume();
        LogWrapper.d(this.getClass().getSimpleName(), "onResume " + this.getClass().getSimpleName());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LogWrapper.d(this.getClass().getSimpleName(), "onPause " + this.getClass().getSimpleName());
    }

    protected void showErrorDialog(int error)
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.proxy_error)
                .setMessage(error)
                .setPositiveButton(R.string.proxy_error_dismiss, null)
                .show();
    }
}
