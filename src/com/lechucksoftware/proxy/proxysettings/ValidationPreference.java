package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;

public class ValidationPreference extends Preference
{
	public ValidationPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setWidgetLayoutResource(R.layout.validation_preference_widget);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInteger(index, 0);
	}
}