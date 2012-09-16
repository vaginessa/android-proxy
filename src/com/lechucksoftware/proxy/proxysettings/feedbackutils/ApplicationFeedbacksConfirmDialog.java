package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ApplicationsFeedbacksActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ApplicationFeedbacksConfirmDialog extends DialogFragment
{
	int mNum;

	private ApplicationFeedbacksConfirmDialog()
	{

	}
	
    public static ApplicationFeedbacksConfirmDialog newInstance() 
    {
    	ApplicationFeedbacksConfirmDialog frag = new ApplicationFeedbacksConfirmDialog();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {	
    	View view = LayoutInflater.from(getActivity()).inflate(R.layout.feedback_submit_dialog, (ViewGroup) getActivity().findViewById(R.id.layout_root));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        
//                .setIcon(R.drawable.alert_dialog_icon)
//                .setTitle(title)
                        
    	builder.setPositiveButton(R.string.accept,
                    new DialogInterface.OnClickListener() 
                	{
                        public void onClick(DialogInterface dialog, int whichButton) 
                        {
                            ((ApplicationsFeedbacksActivity)getActivity()).doPositiveClick();
                        }
                    }
                )
                .setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() 
                	{
                        public void onClick(DialogInterface dialog, int whichButton) 
                        {
                            ((ApplicationsFeedbacksActivity)getActivity()).doNegativeClick();
                        }
                    }
                )
                .create();
    	
    	return builder.create();
    }

}