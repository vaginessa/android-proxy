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

    public static AboutDialog newInstance()
    {
        AboutDialog frag = new AboutDialog();
        return frag;
    }
}
