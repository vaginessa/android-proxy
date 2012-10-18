package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ApplicationsFeedbacksActivity;

public class ApplicationFeedbacksConfirmDialog extends DialogFragment
{
	public ApplicationFeedbacksConfirmDialog()
	{

	}
	
    public static ApplicationFeedbacksConfirmDialog newInstance(PInfo pInfo) 
    {
    	ApplicationFeedbacksConfirmDialog frag = new ApplicationFeedbacksConfirmDialog();
        
    	Bundle args = new Bundle();
        args.putSerializable("appInfo", pInfo);
        frag.setArguments(args);  
        
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {	
    	PInfo appInfo = (PInfo) getArguments().getSerializable("appInfo");
    	
    	View view = LayoutInflater.from(getActivity()).inflate(R.layout.feedback_submit_dialog, (ViewGroup) getActivity().findViewById(R.id.layout_root));
    	ImageView appico = (ImageView) view.findViewById(R.id.feedback_app_icon);
    	appico.setImageDrawable(appInfo.icon);
    	
    	TextView appname = (TextView) view.findViewById(R.id.feedback_app_name);
    	appname.setText(appInfo.appname);
    	
    	TextView pname = (TextView) view.findViewById(R.id.feedback_app_description);
    	pname.setText(appInfo.pname);
    	
    	TextView pvers = (TextView) view.findViewById(R.id.feedback_app_version);
    	pvers.setText(appInfo.versionName);
    	
//    	TextView dialog_accept_desc = (TextView) view.findViewById(R.id.application_dialog_accept_description);
//    	String formatdesc = getActivity().getResources().getString(R.string.application_feedback_dialog_accept_description);
//    	String fulldesc = String.format(formatdesc, appInfo.pname);
//    	dialog_accept_desc.setText(fulldesc);
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        
        builder.setTitle(R.string.application_feedback_dialog_title)
        	   .setPositiveButton(R.string.accept,
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