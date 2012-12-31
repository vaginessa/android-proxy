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
	
	public static String cleanUpSSID(String SSID)
	{
		if (SSID.startsWith("\""))
			return removeDoubleQuotes(SSID);
		else
			return SSID;
	}
	
	public static String removeDoubleQuotes(String string)
	{
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"'))
		{
			return string.substring(1, length - 1);
		}
		return string;
	}

	public static String convertToQuotedString(String string)
	{
		return "\"" + string + "\"";
	}
}
