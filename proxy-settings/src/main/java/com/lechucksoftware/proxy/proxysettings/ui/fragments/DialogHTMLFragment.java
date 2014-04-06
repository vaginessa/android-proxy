package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LocaleManager;

/**
 * Created by mpagliar on 13/03/14.
 */
public class DialogHTMLFragment extends BaseDialogFragment
{
    public static DialogHTMLFragment instance;

    private static final String DIALOG_TITLE_ARG = "DIALOG_TITLE_ARG";
    private static final String DIALOG_HTML_TEXT_ARG = "DIALOG_HTML_TEXT_ARG";
    private static final String DIALOG_CLOSE_ARG = "DIALOG_CLOSE_ARG";
    private static final String DIALOG_DISMISS_LISTENER_ARG = "DIALOG_DISMISS_LISTENER_ARG";
    private String titleText;
    private String htmlText;
    private String closeText;
    private WebView webView;
    private Dialog dialog;
    String BASE_URL = "file:///android_asset/www/www-" + LocaleManager.getTranslatedAssetLanguage() + '/';


    public static DialogHTMLFragment newInstance(String title, String htmlText, String closeString, final DialogInterface.OnDismissListener mOnDismissListener)
    {
        DialogHTMLFragment instance = new DialogHTMLFragment();

        Bundle args = new Bundle();
        args.putSerializable(DIALOG_TITLE_ARG, title);
        args.putSerializable(DIALOG_HTML_TEXT_ARG, htmlText);
        args.putSerializable(DIALOG_CLOSE_ARG, closeString);
//        args.putSerializable(DIALOG_DISMISS_LISTENER_ARG, mOnDismissListener);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null)
        {
            if (args.containsKey(DIALOG_TITLE_ARG))
            {
                titleText = (String) getArguments().getSerializable(DIALOG_TITLE_ARG);
            }

            if (args.containsKey(DIALOG_HTML_TEXT_ARG))
            {
                htmlText = (String) getArguments().getSerializable(DIALOG_HTML_TEXT_ARG);
            }

            if (args.containsKey(DIALOG_CLOSE_ARG))
            {
                closeText = (String) getArguments().getSerializable(DIALOG_CLOSE_ARG);
            }
        }


        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.html_dialog, container, false);

        dialog = getDialog();
        if (dialog != null)
        {
            dialog.setTitle(titleText);
        }

        webView = (WebView) v.findViewById(R.id.dialog_webview);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                try
                {
                    getActivity().startActivity(intent);
                }
                catch (Exception e)
                {
                    EventReportingUtils.sendException(e);
                }

                return true;
            }
        });

//        webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8", null);
        webView.loadUrl(BASE_URL + htmlText);

//        getDialog().set
//                .setPositiveButton(closeString, new Dialog.OnClickListener()
//                {
//                    public void onClick(final DialogInterface dialogInterface, final int i)
//                    {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .setOnCancelListener(new DialogInterface.OnCancelListener()
//                {
//
//                    @Override
//                    public void onCancel(DialogInterface dialog)
//                    {
//                        dialog.dismiss();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
//        {
//            @Override
//            public void onDismiss(final DialogInterface dialog)
//            {
//                if (mOnDismissListener != null)
//                {
//                    mOnDismissListener.onDismiss(dialog);
//                }
//            }
//        });

        return v;
    }

}
