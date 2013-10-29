package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

public class AboutDialog extends DialogPreference
{
    private TextView aboutOpenSource;

    public AboutDialog(Context context, AttributeSet attrs)
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
        View v = vi.inflate(R.layout.about, null);

        TextView aboutVersion = (TextView) v.findViewById(R.id.about_version);
        aboutVersion.setText(Utils.getAppVersionName(getContext()));

//        aboutOpenSource = (TextView) v.findViewById(R.id.about_opensource);
//        String oss = getContext().getString(R.string.about_opensource);
//        Spanned html = Html.fromHtml(oss);
//        aboutOpenSource.setText(html);

        return v;
    }
}
