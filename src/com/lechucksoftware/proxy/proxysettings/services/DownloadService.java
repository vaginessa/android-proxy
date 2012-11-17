package com.lechucksoftware.proxy.proxysettings.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy.Type;
import java.net.URL;

import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.webkit.MimeTypeMap;

public class DownloadService extends IntentService
{
	public static final int UPDATE_PROGRESS = 8344;
	public static String downloadFolder;
	public static String urlToDownload; 
	public static ResultReceiver receiver;

	public DownloadService()
	{
		super("DownloadService");
	}

	public static String GetRemoteResourceFileExtension(HttpURLConnection connection)
	{
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		return mimeTypeMap.getExtensionFromMimeType(connection.getContentType().split(";")[0]);
	}

	public static String GetRemoteResourceFileName(HttpURLConnection connection)
	{
		String raw = connection.getHeaderField("Content-Disposition");
		String fileName;
		
		// raw = "attachment; filename=abc.jpg"
		if (raw != null && raw.indexOf("=") != -1)
		{
			fileName = raw.split("=")[1];
			return fileName;
		}
		else
		{
			fileName = null;
			URL url = connection.getURL();
			fileName = url.getFile();
			if (fileName != null && fileName != "")
			{
				if (fileName.contains("/"))
				{
					if (fileName.split("/").length > 1)
					{
						// Get only last part of page
						String[] splitted = fileName.split("/");
						fileName = splitted[splitted.length - 1];
					}
					else
					{
						fileName = url.getHost().replace(".", "")
												.replace("www", "");					
					}
				}
				
				if (!fileName.contains("."))
				{

					// Add extension to filename
					fileName = fileName.concat("." + GetRemoteResourceFileExtension(connection));
				}

				return fileName;
			}
			else
			{
				return url.getHost().replace(".", "")
									.replace("www", "") + "." + GetRemoteResourceFileExtension(connection);
			}
			// fall back to random generated file name?
		}

	}

	public static String GetLocalFileName(HttpURLConnection connection)
	{
		String startfileName =  downloadFolder + GetRemoteResourceFileName(connection);
		String fileName = startfileName;

		File file = new File(fileName);
		int i = 1;
		while (file.exists())
		{

			fileName = startfileName.split("[.]")[0] + "(" + i++ + ")." + GetRemoteResourceFileExtension(connection);
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

		urlToDownload = intent.getStringExtra("url");
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		downloadFolder = intent.getStringExtra("downloadFolder");
		try
		{
			HttpURLConnection con = null;
			URL url = new URL(urlToDownload);

			if (Globals.getInstance().proxyConf.getConnectionType()==Type.HTTP)
			{
				System.setProperty("http.proxyHost", Globals.getInstance().proxyConf.getProxyIPHost());
				System.setProperty("http.proxyPort", Globals.getInstance().proxyConf.getProxyPort().toString());
			}
			
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(60000);
			con.setConnectTimeout(60000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			InputStream input = con.getInputStream();

			String filetype = con.getContentType();

			// download the file
			fileName = GetLocalFileName(con);
			OutputStream output = new FileOutputStream(fileName);

			byte data[] = new byte[1024];
			
			int count;
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
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Bundle resultData = new Bundle();
		resultData.putLong("downloaded", total);
		resultData.putString("filename", fileName);
		resultData.putBoolean("finish", true);
		receiver.send(UPDATE_PROGRESS, resultData);
	}
}
