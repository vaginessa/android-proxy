package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

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
public class DBUtils
{
    public static void execSQL(SQLiteDatabase db, String sql)
    {
        try
        {
//            Timber.d("EXEC SQL: " + sql);
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during execSQL");
        }
    }

    public static Cursor rawQuery(SQLiteDatabase database, String sql, String [] args)
    {
        Cursor cursor = null;

        try
        {
//            Timber.d("RAW QUERY SQL: '%s', PARAMS '%s'", sql, args != null ? TextUtils.join("', '",args) : "NULL");
            cursor = database.rawQuery(sql, args);
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during rawQuery");
        }

        return cursor;
    }

    public static String backupDB(Context ctx)
    {
        PackageInfo applicationInfo = Utils.getAppInfo(ctx);

        final String inFileName = String.format("/data/data/%s/databases/%s",applicationInfo.packageName, DatabaseSQLiteOpenHelper.DATABASE_NAME);
        File dbFile = new File(inFileName);

        try
        {
            FileInputStream fis = new FileInputStream(dbFile);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
            String outFileName = String.format("%s/proxy_settings_backup_%s.db", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), df.format(new Date()));

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

            String msg = "Proxy Settings DB saved on: " + outFileName;

            Timber.w(msg);
        }
        catch (Exception e)
        {
            String msg ="Exception during DB backup";
            Timber.e(e, msg);
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        }

        return inFileName;
    }

    public static String dumpCursorColumns(Cursor cursor)
    {
        String [] columns = cursor.getColumnNames();
        String [] enColumns = new String[columns.length];

        for (int i=0; i<columns.length; i++)
        {
            enColumns[i] = String.format("'[%d] %s'", i, cursor.getColumnName(i));
        }

        return TextUtils.join(", ", enColumns);
    }
}
