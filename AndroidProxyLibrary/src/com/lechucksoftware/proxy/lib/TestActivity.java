/**
 * 
 */
package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author marco
 *
 */
public class TestActivity extends Activity
{
	public static final String TAG = "TestActivity";
	
	public enum ProxySettings {
        /* No proxy is to be used. Any existing proxy settings
         * should be cleared. */
        NONE,
        /* Use statically configured proxy. Configuration can be accessed
         * with linkProperties */
        STATIC,
        /* no proxy details are assigned, this is used to indicate
         * that any existing proxy settings should be retained */
        UNASSIGNED
    }
	
	static void describeClassOrInterface(Class className, String name) {
	    displayModifiers(className.getModifiers());
	    displayFields(className.getDeclaredFields());
	    displayMethods(className.getDeclaredMethods());

	    if (className.isInterface()) {
	    	Log.d(TAG,"Interface: " + name);
	    } else {
	    	Log.d(TAG,"Class: " + name);
	      displayInterfaces(className.getInterfaces());
	      displayConstructors(className.getDeclaredConstructors());
	    }
	  }

	  static void displayModifiers(int m) {
		  Log.d(TAG,"Modifiers: " + Modifier.toString(m));
	  }

	  static void displayInterfaces(Class[] interfaces) {
	    if (interfaces.length > 0) {
	    	Log.d(TAG,"Interfaces: ");
	      for (int i = 0; i < interfaces.length; ++i)
	    	  Log.d("",interfaces[i].getName());
	    }
	  }

	  static void displayFields(Field[] fields) {
	    if (fields.length > 0) {
	    	Log.d(TAG,"Fields: ");
	      for (int i = 0; i < fields.length; ++i)
	    	  Log.d(TAG,fields[i].toString());
	    }
	  }

	  static void displayConstructors(Constructor[] constructors) {
	    if (constructors.length > 0) {
	    	Log.d(TAG,"Constructors: ");
	      for (int i = 0; i < constructors.length; ++i)
	    	  Log.d(TAG,constructors[i].toString());
	    }
	  }

	  static void displayMethods(Method[] methods) {
	    if (methods.length > 0) {
	    	Log.d(TAG,"Methods: ");
	      for (int i = 0; i < methods.length; ++i)
	    	  Log.d(TAG,methods[i].toString());
	    }
	  }
	  
	    public static Field[] getAllFields(Class klass) {
	        List<Field> fields = new ArrayList<Field>();
	        fields.addAll(Arrays.asList(klass.getDeclaredFields()));
	        if (klass.getSuperclass() != null) {
	            fields.addAll(Arrays.asList(getAllFields(klass.getSuperclass())));
	        }
	        return fields.toArray(new Field[] {});
	    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
   
        
        try
        {
//            ConnectivityManager connMngr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            Field mService = connMngr.getClass().getDeclaredFields()[45];
//            mService.setAccessible(true);
//            Object mServiceObj = mService.get(connMngr);
//            Class IConnectivityManager = Class.forName("android.net.IConnectivityManager");
//            Method getProxyMethod = connMngr.getClass().getDeclaredFields()[45].getType().getMethod("getProxy");
//            Class ProxyPopertiesClass = getProxyMethod.getReturnType();
//            Constructor constructor = ProxyPopertiesClass.getConstructors()[1];
//            Object proxyProperties = constructor.newInstance("aa",8080,"aaa");            
//            Object o = getProxyMethod.invoke(mServiceObj, null);
            
            
            
            WifiConfiguration conf = new WifiConfiguration();
            
            Field linkPropertiesField = conf.getClass().getField("linkProperties");
            Object linkProperties = linkPropertiesField.get(conf);
            Field mHttpProxyField = linkProperties.getClass().getDeclaredFields()[2];
            mHttpProxyField.setAccessible(true);
            Object mHttpProxy = mHttpProxyField.get(linkProperties);
            
            if (mHttpProxy == null)
            {
            	Class ProxyPropertiesClass = mHttpProxyField.getType();
            	Constructor constr = ProxyPropertiesClass.getConstructors()[1];
            	Object ProxyProperties = constr.newInstance("111",0,"111");
            	mHttpProxyField.set(linkProperties, ProxyProperties);
            }
            
//            displayFields(linkProperties.getType().getDeclaredFields());
//            
//            String host = new String();
//            
//            Field mHttpProxy = linkProperties.getType().getDeclaredFields()[2];
//            
//            Method getHostM = mHttpProxy.getType().getDeclaredMethods()[3];
//            Method getPortM = mHttpProxy.getType().getDeclaredMethods()[4];
//            
//            Field mHostF = linkProperties.getType().getDeclaredFields()[2].getType().getDeclaredFields()[2]; //mHost
//            mHostF.setAccessible(true);
//            host = (String) mHostF.get(host);
//            Field mPortF = linkProperties.getType().getDeclaredFields()[2].getType().getDeclaredFields()[4]; //mPort
//            
//            
            
        
        
        
            Log.d(TAG, "-------END--------");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        
        
        
        
//        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//        if(wifiManager.isWifiEnabled())
//        {
//        	WifiInfo info = wifiManager.getConnectionInfo();
//        	List<WifiConfiguration> conf = wifiManager.getConfiguredNetworks();
//        	String currentSSID = info.getSSID();
//        	Log.d("", currentSSID);
//        	
//        	Iterator itr = conf.iterator(); 
//        	
//        	while(itr.hasNext()) 
//        	{
//
//        	    WifiConfiguration element = (WifiConfiguration) itr.next();
////        	    element.linkProperties.getHttpProxy()
//        	    String elementSSID = element.SSID.replace('"', ' ').trim();
//        	    
//        	    Field linkedProperties = null;
//        	    Field httpProxy = null;
//        	    Field httpProxyHost = null;
//        	    Field httpProxyPort = null;
//        	    Method getHostM = null;
//        	    
//        	    try 
//        	    {
//        	    	linkedProperties = element.getClass().getDeclaredField("linkProperties");
//        	    	describeClassOrInterface(linkedProperties.getType(),"linkedProperties");
//        	    	        	    	
//        	    	httpProxy = linkedProperties.getType().getDeclaredField("mHttpProxy");
//        	    	Class ProxyClass = httpProxy.getType();
//        	    	
//
//        	    	getHostM = httpProxy.getType().getMethod("getHost", null);
//        	    	Object o = getHostM.invoke(httpProxy.getType().cast(httpProxy), null);
////					httpProxyHost = httpProxy.getType().getDeclaredField("mHost");
////					String proxyHost = new String();
////					httpProxyHost.setAccessible(true);
////					httpProxyHost.get(proxyHost);
////					httpProxyPort = httpProxy.getType().getDeclaredField("mPort");
////					int proxyPort = -1;
////					httpProxyPort.get(proxyPort);
//					
//					Log.d(TAG, httpProxyHost + ":" + httpProxyPort);
//				} 
//        	    catch (NoSuchFieldException e) 
//        	    {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//        	            	    
//        	    if (elementSSID.equals(currentSSID))
//        	    {
//        	    	Log.d("", element.toString());
//        	    	
//        	    }
//        	    else
//        	    	Log.d("", element.SSID);
//        	    
//
//        	} 
//        }
    }
}