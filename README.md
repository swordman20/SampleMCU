## Android 快速集成创世MCU平台

> MCU（Mobile Client Unit）是基于创世CreMedia8.0系统开发的移动视频监控SDK。通过集成该SDK，可以实时浏览前端监控图像，支持Wifi、4G无线网络，真正实现了领导、客户随时随地进行远程监控，真正做到“运筹帷幄之中，决胜千里之外”。

### 集成MCU
#### 通过Gradle集成SDK
 打开你项目module 下的 build.gradle 文件里面添加如下引用

```java
	implementation 'com.crearo:mcu:1.0.1'
```
实际效果如下：
![在这里插入图片描述](https://tva1.sinaimg.cn/large/0081Kckwly1glz7l5mq3wj30po081myc.jpg)
#### 配置SDK所需要的权限


```java
		<uses-permission android:name="android.permission.INTERNET" />
	    <uses-permission android:name="android.permission.WAKE_LOCK" />
	    <uses-permission android:name="android.permission.RESTART_PACKAGES" />
	    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	    <uses-permission android:name="android.permission.RECORD_AUDIO" />
	    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	    <!--  语音对接需要蓝牙权限  -->
	    <uses-permission android:name="android.permission.BLUETOOTH" />
```


实际效果如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/b6de053a5275621354af9782f43d545d.png#pic_center#pic_center)
**注意**：如果项目使用的是targetSdkVersion 29 或以上，需要在AndroidManifest->application节点下添加文件读取授权

```java
android:requestLegacyExternalStorage="true"
```


####  下载和配置so支持库
*[armeabi-v7a下载地址](https://github.com/swordman20/SampleMCU/blob/master/app/src/main/jniLibs/armeabi-v7a.zip)*
	在 main 目录下创建文件夹 jniLibs (如果有就不需要创建了)，将下载文件的 armeabi-v7a 文件解压复制到这个目录下,如果已经有这个目录，将下载的 so 库复制到这个目录即可。如图所示：

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/a62786d925810ae1d21ad4e9dc650045.png#pic_center)

在这里要记得在你项目module 下的 build.gradle 文件里面android->defaultConfig节点下添加如下配置

```java
			ndk {
	            // 设置支持的SO库架构
	            abiFilters 'armeabi-v7a'
	        }
```

如图所示：
	![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/350081929ccd31d5cdab219e2a3dde6f.png#pic_center)

#### 初始化SDK
 新建Application（如果有就不需要新建了），在onCreate()和onTerminate()做如下配置

```kotlin
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
```

### 登陆MCU平台
由于MCU采用单客户端集成方案，所以需要客户端手动手动登陆，授权成功方可使用MCU
#### 登陆
	MCUEntity.login(LoginInfo info)


 1. 先获取到MCUEntity对象

```kotlin
        client = MCUEntity.getInstance(applicationContext)
        // 设置码率类型为实时流
        client?.setStreamType(VideoParam.STREAM_REALTIME)
        // 设置码率类型为实时流
        client?.setResolution(VideoParam.RESOLUTION_CIF)
```
 2. 创建LoginInfo对象


```kotlin
		val info = LoginInfo()
            info.addr = ip              //平台地址
            info.port = 8866            //平台端口
            info.user = user            //登陆用户名
            info.password = password    //登陆密码
            info.epid = "system"        //企业id
            info.isFixAddr = true       //是否需要穿透网闸 trur穿透，反之亦然
```

 3. 登陆和回调监听


```kotlin
			//登陆
			if (client != null) {
                client!!.logout()       //先登出
                client!!.login(info)    //再登陆
                client!!.setConnectStateCallback(connectStateCallback)  //登陆回调
            }
			 //登陆回调
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
```


#### 播放视频

 1. 创建RenderView


```java
	<com.oem.sdk.mcuclient.RenderView
        android:id="@+id/mRenderView"
        android:layout_width="match_parent"
        android:layout_height="250dp"
        android:layout_centerInParent="true" />
```

 2. 播放视频


```kotlin
mRenderView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
//                mRenderView.setCallback(mRenderCallback)                    //RenderView播放视频回调
//                mRenderView.encVideoStreamCallback = encVideoStreamCallback //视频流监听回调
                val cameras = ResUtils
                        .getCameraResources(puid)           //通过puid获取视频流
                if (cameras == null || 0 >= cameras.size){ //如果视频流不存在
                    startTimer()
                }else{
                    val inputVideo = cameras[0]      // 预览第resIdx个摄像头资源
                    mRenderView.rend(inputVideo)                //开始播放
                    initAudioRes(inputVideo)                    //初始化音频
                }
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mRenderView.stopRend()  //surface销毁时，关闭正在播放的视频资源
                client?.stopOA()      //关闭正在播放的音频资源
            }
        })
```

 3. 初始化音频资源

```kotlin
private fun initAudioRes(inputVideo: InputVideo) {
        val mAudioPlayerCallback = AudioPlayer.AudioPlayerCallback { player, errorCode -> // 根据errorCode处理回调函数
            0
        }
        val mOaManagerCallback: OAManager.OAManagerRenderCallback = object : OAManager.OAManagerRenderCallback {
            override fun onRendError(oam: OAManager, errorCode: Int): Int {
                // 根据errorCode处理回调函数
                return 0
            }

            override fun getLatestTimestamp(): Long {
                return Date().time
            }
        }
        client!!.initAudioResource(inputVideo, mAudioPlayerCallback,
                mOaManagerCallback)
//        client!!.setEncAudioStreamCallback(encAudioStreamCallback)
    }
```
**至此，MCU的简单集成完成，更多功能请参考**[SampleMCU](https://github.com/swordman20/SampleMCU.git)