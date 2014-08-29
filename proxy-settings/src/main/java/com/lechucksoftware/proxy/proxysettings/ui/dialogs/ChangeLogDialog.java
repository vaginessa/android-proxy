package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

public class ChangeLogDialog extends BaseDialogFragment
{
    public static String TAG = ChangeLogDialog.class.getSimpleName();
    public String title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogListView chgList = (ChangeLogListView) layoutInflater.inflate(R.layout.changelog_dialog, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.changelog)
                .setView(chgList)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }
}
