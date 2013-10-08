package com.lechucksoftware.proxy.proxysettings.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class PInfoAdapter extends ArrayAdapter<PInfo> implements SectionIndexer
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

    private static String sections = "abcdefghilmnopqrstuvz";

    @Override
    public Object[] getSections()
    {
        String[] sectionsArr = new String[sections.length()];
        for (int i = 0; i < sections.length(); i++)
            sectionsArr[i] = "" + sections.charAt(i);

        return sectionsArr;
    }

    @Override
    public int getPositionForSection(int section)
    {
        for (int i = 0; i < getCount(); i++)
        {
            String item = this.getItem(i).appname.toLowerCase();
            if (item.charAt(0) == sections.charAt(section))
                return i;
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int i)
    {
        PInfo item = getItem(i);
        char c = item.appname.toLowerCase().charAt(0);
        int index = sections.indexOf(c);
        if (index < 0)
            return 0;
        else
            return index;
//        return sections.indexOf();
//        return 0;
    }

}
