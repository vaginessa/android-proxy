package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechucksoftware.proxy.proxysettings.R;

public class HtmlDialog extends DialogFragment
{
    public static String TAG = HtmlDialog.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.about, container, false);

        getDialog().setTitle(R.string.about);
        return v;
    }

    public static HtmlDialog newInstance(String title, String fileName, String closeString)
    {
        HtmlDialog frag = new HtmlDialog();
        return frag;
    }
}
