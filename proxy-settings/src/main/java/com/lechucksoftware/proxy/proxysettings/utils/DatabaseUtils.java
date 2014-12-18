package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.DatabaseSQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;


/**
 * Created by Marco on 17/08/14.
 */
public class DatabaseUtils
{
    private static final String TAG = DatabaseUtils.class.getSimpleName();

    public static void execSQL(SQLiteDatabase db, String sql)
    {
        try
        {
            Timber.d("EXEC SQL: " + sql);
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during execSQL");
        }
    }

    public static void backupDB(Context ctx)
    {
        PackageInfo applicationInfo = Utils.getAppInfo(ctx);

        final String inFileName = String.format("/data/data/%s/databases/%s",applicationInfo.packageName, DatabaseSQLiteOpenHelper.DATABASE_NAME);
        File dbFile = new File(inFileName);

        try
        {
            FileInputStream fis = new FileInputStream(dbFile);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String outFileName = String.format("%s/proxy_settings_backup_%s.db",Environment.getExternalStorageDirectory(),df.format(new Date()));

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0)
            {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();

            Toast.makeText(ctx, "Proxy Settings DB saved on: " + outFileName, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during DB backup");
        }
    }
}
