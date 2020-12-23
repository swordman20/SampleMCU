package com.ahhwxx.samplemcu;

import android.app.Application;

import com.oem.sdk.base.GlobalHelper;
import com.oem.sdk.base.SdkGlobal;

/**
 * @ClassName: App
 * User: xiaxiaoge
 * Date: 2020/12/21
 * @Description:
 */
public class App extends Application {
    private GlobalHelper mGlobalHelper = null;
    @Override
    public void onCreate() {
        super.onCreate();
        SdkGlobal.setContext(this);
        mGlobalHelper = GlobalHelper.getInstance(getApplicationContext());
        mGlobalHelper.onGlobalInit();
    }

    @Override
    public void onTerminate() {
        mGlobalHelper.onGlobalClose();;
        super.onTerminate();
    }
}
