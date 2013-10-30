package com.lechucksoftware.proxy.proxysettings.test;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;

import java.util.Random;

/**
 * Created by marco on 10/10/13.
 */
public class TestDB
{
    // "0123456789" + "ABCDE...Z"
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MIN_LENGHT = 3;
    private static final int MAX_LENGHT = 15;

    private static final int MIN_TAGS = 0;
    private static final int MAX_TAGS = 6;

    public static String getRandomExclusionList()
    {
        Random r = new Random();
        int maxEx = r.nextInt(10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxEx; i++)
        {
            if (i != 0)
                sb.append(",");
            sb.append(getRandomIP());
        }

        return sb.toString();
    }

    public static String getRandomIP()
    {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }

    public static int getRandomPort()
    {
        Random r = new Random();
        return r.nextInt(65536);
    }

    public static String getRandomTag()
    {
        Random r = new Random();
        int len = r.nextInt(MAX_LENGHT) + MIN_LENGHT;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
        {
            int charpos = r.nextInt(ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(charpos));
        }

        return sb.toString();
    }

    public static DBProxy getRandomProxy()
    {
        DBProxy pd = new DBProxy();
        pd.host = getRandomIP();
        pd.port = getRandomPort();
        pd.exclusion = getRandomExclusionList();

        Random r = new Random();
        int tagNum = r.nextInt(MAX_TAGS);
        for (int i = 0; i < tagNum; i++)
        {
            DBTag tag = new DBTag();
            tag.tag = getRandomTag();
            tag.tagColor = r.nextInt(5) + 1;
            pd.tags.add(tag);
        }

        return pd;
    }

    public static DBProxy getModifiedExistingProxy()
    {
        DBProxy pd = ApplicationGlobals.getDBManager().getRandomProxy();

        if (pd != null)
        {
            Random r = new Random();
            int typeOfModification = r.nextInt(6);

            switch (typeOfModification)
            {
                case 0:
                    pd.host = getRandomIP();
                    break;
                case 1:
                    pd.port = getRandomPort();
                    break;
                case 2:
                    pd.exclusion = getRandomExclusionList();
                    break;
                case 3:
                    DBTag tag = new DBTag();
                    tag.tag = getRandomTag();
                    tag.tagColor = r.nextInt(10);
                    pd.tags.add(tag);
                    break;
                case 4:
                    DBTag tagE = ApplicationGlobals.getDBManager().getRandomTag();
                    pd.tags.add(tagE);
                    break;
                case 5:
                    if (pd.tags.size() > 0)
                    {
                        pd.tags.remove(0);
                    }
                    break;
            }
        }

        return pd;
    }

    public static void addProxy()
    {
        DBProxy pd = getRandomProxy();
        ApplicationGlobals.getDBManager().upsertProxy(pd);
    }

    public static void updateProxy()
    {
        DBProxy pd = getModifiedExistingProxy();
        if (pd != null)
        {
            ApplicationGlobals.getDBManager().upsertProxy(pd);
        }
    }
}
