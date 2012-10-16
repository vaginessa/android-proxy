package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;

public class DisclaimerActivity extends Activity
{
	static final int DIALOG_ID_DISCLAIMER = 0;
	public static final String TAG = "DisclaimerActivity";
	
	public static ScrollView scroller;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
    	scroller = (ScrollView) findViewById(R.id.scroller);
    	
    	showDialog(DIALOG_ID_DISCLAIMER);
    }
    
    protected Dialog onCreateDialog(int id) 
    {
        Dialog dialog;
        switch(id) 
        {
            case DIALOG_ID_DISCLAIMER:
            	           	
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		
        		builder.setTitle(getResources().getText(R.string.disclaimer_title))
        		       .setCancelable(false)
        		       .setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() 
        		       {
        					public void onClick(DialogInterface paramDialogInterface,int paramInt)
        					{
        						finish();
        					}
        		       })
        			   .setPositiveButton(getResources().getText(R.string.accept), new DialogInterface.OnClickListener() 
        			   {
    						public void onClick(DialogInterface paramDialogInterface, int paramInt)
    						{
    				        	SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
    				        	Editor editor = settings.edit(); 
    				            editor.putBoolean(Constants.PREFERENCES_ACCEPTED_DISCLAIMER, true);
    				            editor.commit();
    				            
    				            Intent i = new Intent(getApplicationContext(), ProxySettingsCallerActivity.class);
    				            Log.d(TAG,"Starting ProxySettingsCallerActivity activity");
    				            startActivity(i);
    				            finish();
    						}
        			   });
        		
        		AlertDialog alert = builder.create();
        		       		        		
        		LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.disclaimer, null);
        		alert.setView(textEntryView);
        		dialog = alert;
                break;
                
            default:
                dialog = null;
        }
        
        return dialog;
    }   
}

