package com.lechucksoftware.proxy.proxysettings.excluded_from_build;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import com.lechucksoftware.proxy.proxysettings.activities.WiFiApListActivity;
import com.shouldit.proxy.lib.log.LogWrapper;

public class ProxySettingsCallerActivity extends FragmentActivity
{
	public static String TAG = "ProxySettingsCallerActivity";

	static final int DIALOG_ID_WARNING = 0;
	static final int DIALOG_ID_APP_RATE = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		LogWrapper.d(TAG, "SDK Version");
		LogWrapper.d(TAG, "SDK Version: " + Build.VERSION.SDK_INT);

//        if (showAppRate())
//		{
//			RateApplicationAlertDialog dialog = RateApplicationAlertDialog.newInstance();
//            dialog.show(getSupportFragmentManager(), TAG);
//		}
//		else if (showAppBetaTest())
//        {
//            BetaTestApplicationAlertDialog dialog = BetaTestApplicationAlertDialog.newInstance();
//            dialog.show(getSupportFragmentManager(), TAG);
//        }
//        else
//		{
//			GoToProxy();
//		}
		
//		LogWrapper.d(TAG, "Finish onCreate");
	}

    public void onBackPressed() 
    {
    	LogWrapper.d(TAG, "Back Pressed");
    	return;
    }
    
    @Override
    protected void onStart() {
        super.onStart();
//        LogWrapper.d(TAG, "Start");
    }
    @Override
    protected void onResume() {
        super.onResume();
//        LogWrapper.d(TAG, "Resume");
    }
    @Override
    protected void onPause() {
        super.onPause();
//        LogWrapper.d(TAG, "Pause");
    }
    @Override
    protected void onStop() {
//    	LogWrapper.d(TAG, "Stop");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
//    	LogWrapper.d(TAG, "Destroy");
        super.onDestroy();
    }

	public void GoToProxy()
	{
//		LogWrapper.d(TAG, "Starting WiFiApListActivity activity");
		
		final Intent intent = new Intent(this, WiFiApListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
		finish();
	}
}