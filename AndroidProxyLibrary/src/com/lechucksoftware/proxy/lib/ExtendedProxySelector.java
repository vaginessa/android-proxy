package com.lechucksoftware.proxy.lib;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;

import android.util.Log;

public class ExtendedProxySelector extends ProxySelector
{
	public static final String TAG = "ExtendedProxySelector";
	
	
	// Keep a reference on the previous default
	ProxySelector defsel = null;

	public ExtendedProxySelector(ProxySelector def) 
	{
		// Save the previous default
		defsel = def;
	}

	/*
	 * This is the method that the handlers will call. Returns a List of proxy.
	 */
	public java.util.List<Proxy> select(URI uri)
	{
		// Let's stick to the specs.
		if (uri == null) 
		{
			throw new IllegalArgumentException("URI can't be null.");
		}

		Log.d(TAG, "Selecting right proxy for uri: " + uri.toString());
		
		if (defsel != null) 
		{
			return defsel.select(uri);
		} 
		else 
		{
			ArrayList<Proxy> l = new ArrayList<Proxy>();
			l.add(Proxy.NO_PROXY);
			return l;
		}
	}

	/*
	 * Method called by the handlers when it failed to connect to one of the
	 * proxies returned by select().
	 */
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe)
	{
		defsel.connectFailed(uri, sa, ioe);
	}

}
