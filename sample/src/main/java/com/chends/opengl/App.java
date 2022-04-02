package com.chends.opengl;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author chends create on 2022/4/2.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBugly();
    }

    /**
     * bugly 统计
     */
    private void initBugly() {
        CrashReport.setIsDevelopmentDevice(getBaseContext(), BuildConfig.DEBUG);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getBaseContext());
        Bugly.init(getBaseContext(), Constant.BuglyAppId, BuildConfig.DEBUG, strategy);
    }
}
