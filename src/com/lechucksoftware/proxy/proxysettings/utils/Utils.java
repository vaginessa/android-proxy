package com.lechucksoftware.proxy.proxysettings.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.util.Log;

import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.reflection.ReflectionUtils;
import com.shouldit.proxy.lib.reflection.android.RProxySettings;

public class Utils
{
	public static String TAG = "Utils";
	
	public static void SetHTTPAuthentication(final String user, final String password)
	{
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});
	}
}
