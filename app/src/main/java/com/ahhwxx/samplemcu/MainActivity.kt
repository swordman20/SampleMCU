package com.ahhwxx.samplemcu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.oem.sdk.callbacks.ConnectStateCallback
import com.oem.sdk.mcuclient.MCUEntity
import com.oem.sdk.net.utils.LoginInfo
import com.oem.sdk.utils.VideoParam
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mRequestCode = 100 // 权限请求码

    // 申请权限：文件读写、音频、网络、设备状态
    var permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO)
    var mPermissionList: MutableList<String> = ArrayList()

    // 平台登录参数
    private var client: MCUEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 6.0以上需要动态权限
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission()
        } else {
            init()
        }
    }

    private fun init() {
        client = MCUEntity.getInstance(applicationContext)
        // 设置码率类型为实时流
        client?.setStreamType(VideoParam.STREAM_REALTIME)
        // 设置码率类型为实时流
        client?.setResolution(VideoParam.RESOLUTION_CIF)
        btn_login.setOnClickListener {
            val ip = et_ip.text.toString().trim()
            val user = et_user.text.toString().trim()
            val password = et_password.text.toString().trim()
            doLogin(ip, user, password)
        }
    }

    private fun doLogin(ip: String, user: String, password: String) {
        if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE)
            val info = LoginInfo()
            info.addr = ip              //平台地址
            info.port = 8866            //平台端口
            info.user = user            //登陆用户名
            info.password = password    //登陆密码
            info.epid = "system"        //企业id
            info.isFixAddr = true       //是否需要穿透网闸 trur穿透，反之亦然
            if (client != null) {
                client!!.logout()       //先登出
                client!!.login(info)    //再登陆
                client!!.setConnectStateCallback(connectStateCallback)  //登陆回调
            }
        } else {
            Toast.makeText(this, "输入ip、用户名及密码", Toast.LENGTH_SHORT).show()
        }
    }

    private val connectStateCallback: ConnectStateCallback = object : ConnectStateCallback {
        override fun onLine(stateType: Int) {
            Log.v(this.javaClass.name, "MCU上线")
            runOnUiThread {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "登录成功", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, PUIDActivity::class.java))
                finish()
            }
        }

        override fun offLine(stateType: Int) {
            runOnUiThread {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "登录失败", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(stateType: Int, errorCode: Int) {
            // TODO Auto-generated method stub
        }
    }

    private fun initPermission() {
        mPermissionList.clear()
        // 逐个判断是否还有未通过的权限
        // 逐个判断是否还有未通过的权限
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(this,
                            permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i])
            }
        }
        // 有权限没有通过，需要申请
        // 有权限没有通过，需要申请
        if (mPermissionList.size > 0) {
            ActivityCompat.requestPermissions(this, permissions, mRequestCode)
        } else {
            init()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasPermissionDismiss = false // 有权限没有通过
        if (mRequestCode == requestCode) {
            for (i in grantResults.indices) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true
                    break
                }
            }
        }
        if (hasPermissionDismiss) {  //如果有没有被允许的权限
            Toast.makeText(this, "你没有权限，去设置打开", Toast.LENGTH_SHORT).show()
        } else {
            //权限已经都通过了，可以将程序继续打开了
            init()
        }
    }
}