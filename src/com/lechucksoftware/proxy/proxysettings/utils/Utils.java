package com.lechucksoftware.proxy.proxysettings.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

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
