/*
 * (c) 2012 Martin van Zuilekom (http://martin.cubeactive.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.lechucksoftware.proxy.proxysettings.excluded_from_build;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;


/**
 * Class to show a dialog with the latest changes for the current app version.
 */
public class WhatsNewDialog extends ChangeLogDialog
{
    private static final String WHATS_NEW_LAST_SHOWN = "whats_new_last_shown";
    private static final String TAG = WhatsNewDialog.class.getSimpleName();
    private PackageInfo appInfo;

    public WhatsNewDialog(Activity activity)
    {
        super(activity);
        appInfo = Utils.getAppInfo(getContext());
    }

    public void forceShow()
    {
        //Show only the changes from this version (if available)
        show(appInfo.versionCode);
    }

    @Override
    public void show()
    {
        show(appInfo.versionCode);

        if (mOnDismissListener != null)
        {
            mOnDismissListener.onDismiss(null);
        }
    }

    public Boolean isToShow()
    {
        //ToDo check if version is shown
        final SharedPreferences prefs = getContext().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);

        final int versionShown = prefs.getInt(WHATS_NEW_LAST_SHOWN, 0);

        if (versionShown != appInfo.versionCode)
        {
            //This version is new, show only the changes from this version (if available)

            //Update last shown version
            final SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(WHATS_NEW_LAST_SHOWN, appInfo.versionCode);
            edit.commit();

            return true;
        }
        else
            return false;
    }
}
