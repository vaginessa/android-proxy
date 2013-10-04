package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class PInfoAdapter extends ArrayAdapter<PInfo>
{
    private static String TAG = PInfoAdapter.class.getSimpleName();
    private final LayoutInflater inflater;
    private Context ctx;

    public PInfoAdapter(Context context)
    {
        super(context, R.layout.application_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    public void setData(List<PInfo> pInfoList)
    {
        clear();
        if (pInfoList != null)
        {
            for (PInfo pInfo : pInfoList)
            {
                add(pInfo);
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        try
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.application_list_item, null);
            }

            final PInfo listItem = (PInfo) getItem(position);

            if (listItem != null)
            {
                ((ImageView) view.findViewById(R.id.list_item_app_icon)).setImageDrawable(listItem.icon);
                ((TextView) view.findViewById(R.id.list_item_app_name)).setText(listItem.appname);
                ((TextView) view.findViewById(R.id.list_item_app_description)).setText(listItem.pname);
            }
        }
        catch (Exception e)
        {
            LogWrapper.i(TAG, e.toString());
        }
        return view;
    }

}
