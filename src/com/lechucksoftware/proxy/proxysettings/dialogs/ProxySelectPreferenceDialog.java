package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

public class ProxySelectPreferenceDialog extends DialogPreference
{
    public ProxySelectPreferenceDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(false);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNegativeButton(null, null);
    }

    @Override
    public View onCreateDialogView()
    {
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.proxy_list_dialog, null);
        return v;
    }
}
