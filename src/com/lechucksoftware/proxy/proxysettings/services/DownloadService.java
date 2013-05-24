package com.lechucksoftware.proxy.proxysettings.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy.Type;
import java.net.URL;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

public class DownloadService extends IntentService
{
	public static final int UPDATE_PROGRESS = 1;
	public static final int UPDATE_EXCEPTION = 2;
	
	public static String downloadFolder;
	public static URL urlToDownload; 
	public static ResultReceiver receiver;
	public SharedPreferences sharedPref;

	public DownloadService()
	{
		super("DownloadService");
	}

//	public static String GetRemoteResourceFileExtension(HttpURLConnection connection)
//	{
//		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//		String contentType = connection.getContentType();
//		
//		if (contentType != null && contentType.contains(";"))
//			return mimeTypeMap.getExtensionFromMimeType(contentType.split(";")[0]);
//		else
//			return "htm";
//	}
	
	public static String GetRemoteResourceFileName(HttpURLConnection connection)
	{
		String raw = connection.getHeaderField("Content-Disposition");
		String mimeType = connection.getContentType().split(";")[0];
		String fileName = URLUtil.guessFileName(connection.getURL().toString(), raw, mimeType);
		return fileName;
	}

	public static String GetLocalFileName(HttpURLConnection connection)
	{
		String remoteFileName = GetRemoteResourceFileName(connection);
		String startfileName = downloadFolder + remoteFileName;
		String fileExtension = remoteFileName.split("\\.")[1];
		
		String fileName = startfileName;

		File file = new File(fileName);
		int i = 1;
		while (file.exists())
		{

			fileName = startfileName.split("[.]")[0] + "(" + i++ + ")." + fileExtension;
			file = new File(fileName);
		}

		return fileName;
	}

	// @TargetApi(8)
	@Override
	protected void onHandleIntent(Intent intent)
	{
		long total = 0;
		
		String fileName = "";

		Bundle extras = intent.getExtras();
		urlToDownload = (URL) extras.getSerializable("URL");
		receiver = (ResultReceiver) extras.getParcelable("receiver");
		downloadFolder = extras.getString("downloadFolder");
		
		try
		{
			HttpURLConnection con = null;

			if (ApplicationGlobals.getInstance().getCachedConfiguration().getProxyType()==Type.HTTP)
			{
				System.setProperty("http.proxyHost", ApplicationGlobals.getInstance().getCachedConfiguration().getProxyIPHost());
				System.setProperty("http.proxyPort", ApplicationGlobals.getInstance().getCachedConfiguration().getProxyPort().toString());
			}
			else
			{
				System.setProperty("http.proxyHost", "");
				System.setProperty("http.proxyPort", "");
			}
			
			con = (HttpURLConnection) urlToDownload.openConnection();
			con.setReadTimeout(60000);
			con.setConnectTimeout(60000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			InputStream input = con.getInputStream();

			// download the file
			fileName = GetLocalFileName(con);
					
			OutputStream output = new FileOutputStream(fileName);

			byte data[] = new byte[1024];
			
			int count = 0;
			total = 0;
			while ((count = input.read(data)) != -1)
			{
				total += count;
				// publishing the progress....
				Bundle resultData = new Bundle();
				resultData.putLong("downloaded", total);
				resultData.putString("filename", fileName);
				resultData.putBoolean("finish", false);
				receiver.send(UPDATE_PROGRESS, resultData);
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			Bundle resultData = new Bundle();			
			resultData.putSerializable("exception", e);
			receiver.send(UPDATE_EXCEPTION, resultData);
			return;
		}

		Bundle resultData = new Bundle();
		resultData.putLong("downloaded", total);
		resultData.putString("filename", fileName);
		resultData.putBoolean("finish", true);
		receiver.send(UPDATE_PROGRESS, resultData);
	}
}
