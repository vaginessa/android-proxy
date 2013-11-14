package com.lechucksoftware.proxy.proxysettings.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;

/**
 * Created by marco on 12/09/13.
 */
public class TagView extends TextView
{
    private LinearLayout tagsContainer;

    public TagView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View v = null;
//
//        if(inflater != null)
//        {
////            v = inflater.inflate(R.layout.tags_multiline, this);
//            tagsContainer = (LinearLayout) v.findViewById(R.id.tags_container);
//        }
    }
}
