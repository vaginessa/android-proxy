package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lechucksoftware.proxy.proxysettings.R;

/**
 * Created by marco on 21/05/13.
 */
public class StatusFragment extends Fragment
{
    public static StatusFragment instance;
    private Button statusButton;

    /**
     * Create a new instance of StatusFragment
     */
    public static StatusFragment getInstance()
    {
        if (instance == null)
            instance = new StatusFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.status, container, false);
        statusButton = (Button) view.findViewById(R.id.status_button);
        statusButton.setBackgroundResource(R.color.Holo_Green_Light);
        return view;
    }

    public void refreshUI()
    {

    }
}
