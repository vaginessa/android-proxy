package com.lechucksoftware.proxy.proxysettings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.lechucksoftware.proxy.proxysettings.R;
import be.shouldit.proxy.lib.enums.CheckStatusValues;
import be.shouldit.proxy.lib.ProxyStatusItem;

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
		if (st.effective)
		{
			setEnabled(true);
			
			if (st.status == CheckStatusValues.CHECKING)
			{
				setWidgetLayoutResource(R.layout.actionbar_refresh_progress);
			}
			else
			{
				if (st.result == true)
				{
					setWidgetLayoutResource(R.layout.validation_preference_widget);
					mIcon = getContext().getResources().getDrawable(R.drawable.ic_valid);
				}
				else
				{
					setWidgetLayoutResource(R.layout.validation_preference_widget);
					mIcon = getContext().getResources().getDrawable(R.drawable.ic_error);
				}
			}
		}
		else
		{
			setEnabled(false);
			setWidgetLayoutResource(0);
			setSummary(getContext().getResources().getString(R.string.not_available));
		}

		if (!TextUtils.isEmpty(st.message))
		{
			setSummary(st.message);
		}

		notifyChanged();
	}
}