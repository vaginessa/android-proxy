package com.lechucksoftware.proxy.proxysettings.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.webkit.MimeTypeMap;

public class DownloadService extends IntentService
{
	public static final int UPDATE_PROGRESS = 8344;
	public static String fileName;

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
				return url.toString() + "." + GetRemoteResourceFileExtension(connection);
			}
			// fall back to random generated file name?
		}

	}

	public static String GetLocalFileName(HttpURLConnection connection)
	{
		String startfileName = "/mnt/sdcard/Download/" + GetRemoteResourceFileName(connection);
		fileName = startfileName;

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
		// File f =
		// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		int fileLength = 0;
		String urlToDownload = intent.getStringExtra("url");
		ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		try
		{
			HttpURLConnection con = null;
			URL url = new URL(urlToDownload);

			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			InputStream input = con.getInputStream();

			// this will be useful so that you can show a typical 0-100%
			// progress bar

			fileLength = con.getContentLength();
			String filetype = con.getContentType();

			// download the file
			OutputStream output = new FileOutputStream(GetLocalFileName(con));

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1)
			{
				total += count;
				// publishing the progress....
				Bundle resultData = new Bundle();
				resultData.putInt("progress", (int) (total * 100 / fileLength));
				resultData.putInt("total", fileLength);
				resultData.putString("filename", fileName);
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
		resultData.putInt("progress", 100);
		resultData.putInt("total", fileLength);
		resultData.putString("filename", fileName);
		receiver.send(UPDATE_PROGRESS, resultData);
	}
}
