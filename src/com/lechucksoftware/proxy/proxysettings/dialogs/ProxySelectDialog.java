package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;

import java.util.List;

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
