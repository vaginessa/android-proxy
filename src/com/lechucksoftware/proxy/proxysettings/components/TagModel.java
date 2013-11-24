package com.lechucksoftware.proxy.proxysettings.components;

import com.lechucksoftware.proxy.proxysettings.db.DBTag;

/**
 * Created by Marco on 24/11/13.
 */
public class TagModel
{
    public DBTag tag;
    public boolean isSelected;

    public TagModel(DBTag t, boolean selected)
    {
        tag = t;
        isSelected = selected;
    }
}
