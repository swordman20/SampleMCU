## Android 快速集成创世MCU平台

> MCU（Mobile Client Unit）是基于创世CreMedia8.0系统开发的移动视频监控SDK。通过集成该SDK，可以实时浏览前端监控图像，支持Wifi、4G无线网络，真正实现了领导、客户随时随地进行远程监控，真正做到“运筹帷幄之中，决胜千里之外”。

### 集成MCU

 1. 打开你项目module 下的 build.gradle 文件里面添加如下引用

	```
	implementation 'com.crearo:mcu:1.0.0'
	```
	实际效果如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/c7f4f8c012b43d0ba08daa7a69dbdd7e.png#pic_center)

 2. 配置SDK所需要的权限


	```
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
	![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/b6de053a5275621354af9782f43d545d.png#pic_center)


 3. 下载和配置so支持库
	*[armeabi-v7a下载地址](https://github.com/swordman20/SampleMCU/blob/master/app/src/main/jniLibs/armeabi-v7a.zip)*
	在 main 目录下创建文件夹 jniLibs (如果有就不需要创建了)，将下载文件的 armeabi-v7a 文件解压复制到这个目录下,如果已经有这个目录，将下载的 so 库复制到这个目录即可。如图所示：
	![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/a62786d925810ae1d21ad4e9dc650045.png#pic_center)
	在这里要记得在你项目module 下的 build.gradle 文件里面android->defaultConfig节点下添加如下配置


	```
		ndk {
	            // 设置支持的SO库架构
	            abiFilters 'armeabi-v7a'
	        }
	```
	如图所示：
	![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/350081929ccd31d5cdab219e2a3dde6f.png#pic_center)

 4. 初始化SDK

