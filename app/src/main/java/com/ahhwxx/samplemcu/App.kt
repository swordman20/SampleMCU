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
        //向SdkGlobal传递context对象
        SdkGlobal.setContext(this)
        //使用单例模式获得GlobalHelper对象
        mGlobalHelper = GlobalHelper.getInstance(applicationContext)
        //初始化sdk
        mGlobalHelper?.onGlobalInit()
    }

    override fun onTerminate() {
        //释放GlobalHelper对象
        mGlobalHelper!!.onGlobalClose()
        super.onTerminate()
    }
}