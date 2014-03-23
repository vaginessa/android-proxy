package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.log.LogWrapper;

public class RateApplicationAlertDialog extends DialogFragment
{
    public static String TAG = "RateApplicationAlertDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setTitle(getResources().getString(R.string.app_rater_dialog_title));
        builder.setMessage(getResources().getString(R.string.app_rater_dialog_text));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getText(R.string.app_rater_dialog_button_rate), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                dontDisplayAgainAppRate();
                LogWrapper.d(TAG, "Starting Market activity");

                Utils.startMarketActivity(getActivity());
            }
        });

        builder.setNeutralButton(getResources().getText(R.string.app_rater_dialog_button_remind), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

            }
        });

        builder.setNegativeButton(getResources().getText(R.string.app_rater_dialog_button_nothanks), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                dontDisplayAgainAppRate();
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    public void dontDisplayAgainAppRate()
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        if (editor != null)
        {
            editor.putBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, true);
            editor.commit();
        }
    }

    public static RateApplicationAlertDialog newInstance()
    {
        RateApplicationAlertDialog frag = new RateApplicationAlertDialog();
        return frag;
    }
}
