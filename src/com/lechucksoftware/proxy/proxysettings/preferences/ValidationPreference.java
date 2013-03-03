package com.lechucksoftware.proxy.proxysettings.preferences;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.R.drawable;
import com.lechucksoftware.proxy.proxysettings.R.id;
import com.lechucksoftware.proxy.proxysettings.R.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ValidationPreference extends Preference
{
	private ValidationStatus status;
	private ProgressDialog progress;
	private Drawable mIcon;
	
	public enum ValidationStatus
	{
		CHECKING, VALID, ERROR
	}

	public ValidationPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		status = ValidationStatus.CHECKING;
		setWidgetLayoutResource(R.layout.validation_preference_widget);		
	    mIcon = getContext().getResources().getDrawable(R.drawable.waiting);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInteger(index, 0);
	}

	@Override
	public void onBindView(View view)
	{
		super.onBindView(view);
		ImageView imageView = (ImageView) view.findViewById(R.id.validation_preference_imageview);
		
		if (imageView != null && mIcon != null)
		{
			imageView.setImageDrawable(mIcon);
		}
	}

    public Drawable getIcon() 
    {
        return mIcon;
    }

	public void SetStatus(ValidationStatus st)
	{
		status = st;
		
		if (st == ValidationStatus.CHECKING)
		{
			setWidgetLayoutResource(R.layout.actionbar_refresh_progress);	
		}
		else if (status == ValidationStatus.VALID)
		{
			setWidgetLayoutResource(R.layout.validation_preference_widget);	
			mIcon = getContext().getResources().getDrawable(R.drawable.ok);
		}
		else
		{
			setWidgetLayoutResource(R.layout.validation_preference_widget);	
			mIcon = getContext().getResources().getDrawable(R.drawable.problem);
		}
		
		notifyChanged();
	}
}