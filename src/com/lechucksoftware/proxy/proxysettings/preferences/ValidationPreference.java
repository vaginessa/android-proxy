package com.lechucksoftware.proxy.proxysettings.preferences;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.R.drawable;
import com.lechucksoftware.proxy.proxysettings.R.id;
import com.lechucksoftware.proxy.proxysettings.R.layout;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.ProxyStatusItem;

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
	private Drawable mIcon;

	public enum ValidationStatus
	{
		CHECKING, VALID, ERROR
	}

	public ValidationPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setWidgetLayoutResource(R.layout.actionbar_refresh_progress);
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

	public void SetStatus(ProxyStatusItem st)
	{
		if (st.status == CheckStatusValues.CHECKING)
		{
			setWidgetLayoutResource(R.layout.actionbar_refresh_progress);
		}
		else
		{
			if (st.result == true)
			{
				setWidgetLayoutResource(R.layout.validation_preference_widget);
				mIcon = getContext().getResources().getDrawable(R.drawable.ic_action_valid);
			}
			else
			{
				setWidgetLayoutResource(R.layout.validation_preference_widget);
				mIcon = getContext().getResources().getDrawable(R.drawable.ic_action_notvalid);
			}
		}

		if (st.message != null && st.message.length() > 0)
		{
			setSummary(st.message);
		}
		
		notifyChanged();
	}
}