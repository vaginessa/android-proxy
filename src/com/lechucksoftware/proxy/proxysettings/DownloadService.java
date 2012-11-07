package com.lechucksoftware.proxy.proxysettings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

public class DownloadService extends IntentService
{
	public static final int UPDATE_PROGRESS = 8344;

	public DownloadService()
	{
		super("DownloadService");
	}

//	@TargetApi(8)
	@Override
	protected void onHandleIntent(Intent intent)
	{
//		File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		
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
			int fileLength = con.getContentLength();
			String filetype = con.getContentType();

			// download the file
			OutputStream output = new FileOutputStream("/mnt/sdcard/Download/index.html");

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1)
			{
				total += count;
				// publishing the progress....
				Bundle resultData = new Bundle();
				resultData.putInt("progress", (int) (total * 100 / fileLength));
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
		receiver.send(UPDATE_PROGRESS, resultData);
	}
}
