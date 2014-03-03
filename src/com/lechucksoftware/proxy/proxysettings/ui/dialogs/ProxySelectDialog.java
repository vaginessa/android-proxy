package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lechucksoftware.proxy.proxysettings.R;

public class ProxySelectDialog extends DialogFragment
{
    /**
     * Create a new instance of ProxySelectDialog
     */
    public static ProxySelectDialog newInstance()
    {
        ProxySelectDialog f = new ProxySelectDialog();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.proxy_list_dialog, container, false);
        return v;
    }
}
