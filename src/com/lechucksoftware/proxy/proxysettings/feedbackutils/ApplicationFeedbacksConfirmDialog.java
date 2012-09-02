package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import com.lechucksoftware.proxy.proxysettings.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ApplicationFeedbacksConfirmDialog extends DialogFragment
{
	
	    public ApplicationFeedbacksConfirmDialog() 
	    {

	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	    {
	        View view = inflater.inflate(R.layout.feedback_submit_dialog, container);
	        getDialog().setTitle("Test: Get URI - Obtained result:");
	        
	        TextView t = (TextView) view.findViewById(R.id.submit_dialog_title);
	        t.setText("title");

	        return view;
	    }
	
}
