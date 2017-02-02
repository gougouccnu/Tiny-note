package com.ggccnu.tinynote.view;

import android.app.Application;

import com.blankj.utilcode.utils.LogUtils;

import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.xiaomi.mistatistic.sdk.URLStatsRecorder;
import com.xiaomi.mistatistic.sdk.controller.HttpEventFilter;
import com.xiaomi.mistatistic.sdk.data.HttpEvent;

import org.json.JSONException;

/**
 * Created by lishaowei on 2017/1/31.
 */

public class SplashActivity extends Application {

    private String appID = "2882303761517476332";
    private String appKey = "5721747680332";

    @Override
    public void onCreate() {
        super.onCreate();

        MistatInit();
    }

    private void MistatInit() {
        // regular stats.
        MiStatInterface.initialize(this.getApplicationContext(), appID, appKey,
                "mi");
        MiStatInterface.setUploadPolicy(
                MiStatInterface.UPLOAD_POLICY_REALTIME, 0);
        MiStatInterface.enableLog();

        // enable exception catcher.
        MiStatInterface.enableExceptionCatcher(true);

        // enable network monitor
        URLStatsRecorder.enableAutoRecord();
        URLStatsRecorder.setEventFilter(new HttpEventFilter() {

            @Override
            public HttpEvent onEvent(HttpEvent event) {
                try {
                    LogUtils.d("MI_STAT", event.getUrl() + " result =" + event.toJSON());
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtils.e("MI_STAT", "event null");
                }
                // returns null if you want to drop this event.
                // you can modify it here too.
                return event;
            }
        });

        LogUtils.d("MI_STAT", MiStatInterface.getDeviceID(this) + " is the device.");
    }
}
