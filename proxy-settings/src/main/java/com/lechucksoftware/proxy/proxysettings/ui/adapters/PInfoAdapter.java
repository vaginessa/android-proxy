package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;

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
                listItem.icon = listItem.applicationInfo.loadIcon(ctx.getPackageManager());

                ((ImageView) view.findViewById(R.id.list_item_app_icon)).setImageDrawable(listItem.icon);
                ((TextView) view.findViewById(R.id.list_item_app_name)).setText(listItem.appname);
                ((TextView) view.findViewById(R.id.list_item_app_description)).setText(listItem.pname);
            }
        }
        catch (Exception e)
        {
            App.getLogger().i(TAG, e.toString());
        }
        return view;
    }

    private static String sections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

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
        int position = -1;
        String sectionChar = sections.substring(section,section+1);

        for (int i = 0; i < getCount(); i++)
        {
            if (getItem(i).appname.startsWith(sectionChar))
            {
                position = i;
                break;
            }
            else
                continue;
        }

        while(position == -1)
        {
            position = getPositionForSection(section - 1);
        }

        App.getLogger().d(TAG, String.format("Section %d (%s) -> Position %d", section, sectionChar, position));
        return position;
    }

    @Override
    public int getSectionForPosition(int position)
    {
        PInfo item = getItem(position);
        char c = item.appname.toUpperCase().charAt(0);
        int index = sections.indexOf(c);
        int section = 0;

        if (index < 0)
            section = 0;
        else
            section = index;

        App.getLogger().d(TAG, String.format("Position %d (%c) -> Section %d", position, c, section));
        return section;
    }

}
