package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

public class AboutDialog extends DialogFragment
{
    public static String TAG = AboutDialog.class.getSimpleName();
    private TextView aboutVersionTextView;
    private TextView aboutOSSTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.about, container, false);

        getDialog().setTitle(R.string.about);

        aboutVersionTextView = (TextView) v.findViewById(R.id.about_version);
        aboutOSSTextView = (TextView) v.findViewById(R.id.about_opensource);

        aboutVersionTextView.setText(getResources().getString(R.string.app_versionname, Utils.getAppVersionName(getActivity())));


        // TODO: Use spannable: http://eazyprogramming.blogspot.it/2013/06/spannable-string-in-android-url-span.html
        Spanned spanned = Html.fromHtml(getResources().getString(R.string.about_opensource));
        aboutOSSTextView.setText(spanned);

        // Watch for button clicks.
        Button button = (Button) v.findViewById(R.id.about_close);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                dismiss();
            }
        });

        return v;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
//        builder.setTitle(R.string.about);
//
//        builder.setMessage(R.string.beta_testing_request);
//
//        builder.setCancelable(false);
//
//        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface paramDialogInterface, int paramInt)
//            {
//                EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
//                        R.string.analytics_act_dialog_button_click,
//                        R.string.analytics_lab_beta_test_dialog, 1L);
//
//                BetaTestCommunityDialog betaTestCommunityDialog = BetaTestCommunityDialog.newInstance();
//                betaTestCommunityDialog.show(getFragmentManager(), "BetaTestCommunityDialog");
//            }
//        });
//
//        builder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface paramDialogInterface, int paramInt)
//            {
//                EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
//                        R.string.analytics_act_dialog_button_click,
//                        R.string.analytics_lab_beta_test_dialog, 0L);
//
//                BetaTestDismissedDialog betaTestDismissedDialog = BetaTestDismissedDialog.newInstance();
//                betaTestDismissedDialog.show(getFragmentManager(), "BetaTestDismissedDialog");
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        return alert;
//    }

//    @Override
//    public void onCancel(DialogInterface dialog)
//    {
//        super.onCancel(dialog);
//
//        EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
//                R.string.analytics_act_dialog_button_click,
//                R.string.analytics_lab_beta_test_dialog, 2L);
//    }

    public static AboutDialog newInstance()
    {
        AboutDialog frag = new AboutDialog();
        return frag;
    }
}
