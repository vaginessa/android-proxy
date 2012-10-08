package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;

public class ValidationPreference extends Preference
{
	public enum ValidationStatus
	{
		Checking,
		Valid,
		Error
	}
	
	
	public ValidationPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setWidgetLayoutResource(R.layout.validation_preference_widget_waiting);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInteger(index, 0);
	}
	
	public void SetStatus(ValidationStatus status)
	{
		if (status == ValidationStatus.Checking)
		{
			setWidgetLayoutResource(R.layout.validation_preference_widget_waiting);
		}
		else if (status == ValidationStatus.Valid)
		{
			setWidgetLayoutResource(R.layout.validation_preference_widget_ok);
		}
		else
		{
			setWidgetLayoutResource(R.layout.validation_preference_widget_nok);
		}	
	}
}