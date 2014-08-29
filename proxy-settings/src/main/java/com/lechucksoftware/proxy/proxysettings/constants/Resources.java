package com.lechucksoftware.proxy.proxysettings.constants;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.utils.LocaleManager;

/**
 * Created by Marco on 30/05/14.
 */
public class Resources
{
    public static final String ASSETS = "file:///android_asset/";
    public static final String BASE_WWW = ASSETS + "www/";
    public static final String BASE_LOCALIZED_WWW = BASE_WWW + "www-" + LocaleManager.getTranslatedAssetLanguage() + "/";
    public static final String ABOUT = BASE_LOCALIZED_WWW + "about.html";

    public static final String CHANGELOG_HTML =  BASE_LOCALIZED_WWW + "changelog.html";
    public static final String WHATSNEW_2_16_HTML = BASE_LOCALIZED_WWW + "whats_new_2_16.html";

    public static final String getWhatsNewHTML()
    {
        String resource = null;

        switch (App.getAppMajorVersion())
        {
            case 1300216:
                resource = WHATSNEW_2_16_HTML;
                break;
        }

        return  resource;
    }

}
