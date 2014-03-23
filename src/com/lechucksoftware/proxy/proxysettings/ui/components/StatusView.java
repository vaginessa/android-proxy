package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StatusFragmentStates;

/**
 * Created by marco on 12/09/13.
 */
public class StatusView extends LinearLayout
{
    private Button statusButton;

    public StatusView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = null;
        statusButton = null;

        if(inflater != null)
        {
            v = inflater.inflate(R.layout.status, this);
            statusButton = (Button) v.findViewById(R.id.status_button);
        }
    }

    public void setStatus(StatusFragmentStates status, String message, View.OnClickListener listener, Boolean isInProgress)
    {
//        if (status == clickedStatus)
//        {
//            return;
//        }

        switch (status)
        {
            case CONNECTED:
                setStatusInternal(message, listener, R.drawable.btn_blue_holo_dark,true);
                break;
            case CHECKING:
                setStatusInternal(message, listener, R.drawable.btn_blue_holo_dark,false);
                break;
            case CONNECT_TO:
                setStatusInternal(message, listener, R.drawable.btn_green_holo_dark,true);
                break;
            case NOT_AVAILABLE:
                setStatusInternal(message, listener, R.drawable.btn_blue_holo_dark,false);
                break;
            case ENABLE_WIFI:
                setStatusInternal(message, listener, R.drawable.btn_red_holo_dark,true);
                break;
            case GOTO_AVAILABLE_WIFI:
                setStatusInternal(message, listener, R.drawable.btn_green_holo_dark,true);
                break;
            case NONE:
//            default:
//                hide();
        }
    }

    private void setStatusInternal(String status, View.OnClickListener listener, int resId, boolean enabled)
    {
//        clickedStatus = null;

        if (listener != null)
            statusButton.setText(String.format("%s...", status));
        else
            statusButton.setText(status);

        statusButton.setBackgroundResource(resId);
        statusButton.setEnabled(enabled);
        statusButton.setOnClickListener(listener);
//        show();
    }
}
