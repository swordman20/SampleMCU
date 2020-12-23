package com.ahhwxx.samplemcu

import android.app.Application
import com.oem.sdk.base.GlobalHelper
import com.oem.sdk.base.SdkGlobal

/**
 * @ClassName: App
 * User: xiaxiaoge
 * Date: 2020/12/21
 * @Description:
 */
class App : Application() {
    private var mGlobalHelper: GlobalHelper? = null
    override fun onCreate() {
        super.onCreate()
        SdkGlobal.setContext(this)
        mGlobalHelper = GlobalHelper.getInstance(applicationContext)
        mGlobalHelper?.onGlobalInit()
    }

    override fun onTerminate() {
        mGlobalHelper!!.onGlobalClose()
        super.onTerminate()
    }
}