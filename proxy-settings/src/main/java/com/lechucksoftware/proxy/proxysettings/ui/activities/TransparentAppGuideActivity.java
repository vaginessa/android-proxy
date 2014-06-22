package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;

/**
 * Created by Marco on 22/06/14.
 */
public class TransparentAppGuideActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_screen_layout);

        findViewById(R.id.ok_btn);
    }
}
