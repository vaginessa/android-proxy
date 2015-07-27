package com.lechucksoftware.proxy.proxysettings.ui.fragments.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;

/**
 * Created by Marco on 27/07/15.
 */
public class BaseSlide extends BaseFragment
{
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    public static BaseSlide newInstance(int layoutResId)
    {
        BaseSlide sampleSlide = new BaseSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    private int layoutResId;

    public BaseSlide() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(layoutResId, container, false);
    }
}
