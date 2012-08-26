package com.lechucksoftware.proxy.proxysettings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class UninstallerActivity extends Activity
{
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
        Uri packageURI = Uri.parse("package:com.lechucksoftware.proxy.proxysettings");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }
}
