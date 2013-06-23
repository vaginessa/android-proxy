package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.lechucksoftware.proxy.proxysettings.R;

public class AboutDialog extends DialogPreference
{
    public AboutDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(false);
    }

    @Override
    public View onCreateDialogView()
    {
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.about, null);

        return v;
    }
}
