package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.shouldit.proxy.lib.utils.ProxyUtils;

public class ProxySelectorDialog extends DialogPreference
{
	public ProxySelectorDialog(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setPersistent(false);
	}
	
	@Override
	protected void onClick()
	{
		super.onClick();
	
		getDialog().cancel();
		Intent proxyIntent = ProxyUtils.getProxyIntent();
		getContext().startActivity(proxyIntent);
	}
}
