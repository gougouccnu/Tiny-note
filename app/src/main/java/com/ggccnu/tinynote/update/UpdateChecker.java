package com.ggccnu.tinynote.update;

import android.content.Context;

import com.blankj.utilcode.utils.LogUtils;

public class UpdateChecker {

    public static void checkForDialog(Context context) {
        if (context != null) {
            new CheckUpdateTask(context, Constants.TYPE_DIALOG, false).execute();
        } else {
            LogUtils.e(Constants.TAG, "The arg context is null");
        }
    }


    public static void checkForNotification(Context context) {
        if (context != null) {
            new CheckUpdateTask(context, Constants.TYPE_NOTIFICATION, false).execute();
        } else {
            LogUtils.e(Constants.TAG, "The arg context is null");
        }

    }


}
