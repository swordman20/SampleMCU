package com.ahhwxx.samplemcu

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.oem.sdk.callbacks.EncAudioStreamCallback
import com.oem.sdk.callbacks.EncVideoStreamCallback
import com.oem.sdk.callbacks.NetStatusCallback
import com.oem.sdk.callbacks.RenderCallback
import com.oem.sdk.mcuclient.MCUEntity
import com.oem.sdk.res.ClientRes
import com.oem.sdk.res.InputVideo
import com.oem.sdk.res.utils.ResUtils
import com.oem.sdk.utils.AudioPlayer
import com.oem.sdk.utils.AudioResManager
import com.oem.sdk.utils.OAManager
import kotlinx.android.synthetic.main.activity_play_video_actitity.*
import java.util.*

/**
 * 播放视频的界面
 */
class PlayVideoActitity : AppCompatActivity(), View.OnClickListener {
    private final val TAG: String = "PlayVideoActitity"
    private var client: MCUEntity? = null
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video_actitity)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        ivBack.setOnClickListener(this)
        btnDuijiang.setOnClickListener(this)
        btnMianti.setOnClickListener(this)
        initData()
    }


    private fun initData() {
        var puid = intent.getStringExtra("puid")

        client = MCUEntity.getInstance(applicationContext)
        if (!TextUtils.isEmpty(puid)) {
            playVideo(puid)
        } else {
            startTimer()
        }
    }


    private fun playVideo(puid: String) {
        mRenderView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mRenderView.setCallback(mRenderCallback)
                mRenderView.encVideoStreamCallback = encVideoStreamCallback
                val cameras = ResUtils
                        .getCameraResources(puid)
                if (cameras == null || 0 >= cameras.size) return
                val inputVideo = cameras[0] // 预览第resIdx个摄像头资源
                mRenderView.rend(inputVideo)
                initAudioRes(inputVideo)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mRenderView.stopRend()
                client?.stopPlay()
            }
        })
        btnDownAudio.setOnTouchListener(OnTouchListener { v, event ->
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) { // 按下
                client!!.handleCall()
                btnDownAudio.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.icon_audio_down), null, null)
            } else if (action == MotionEvent.ACTION_UP) { // 松开
                client!!.handleCall()
                btnDownAudio.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.icon_audio_up), null, null)
            }
            false
        })
    }

    /**
     * 初始化音频资源
     *
     * @param inputVideo
     */
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
        client!!.setEncAudioStreamCallback(encAudioStreamCallback)
    }

    var encAudioStreamCallback = EncAudioStreamCallback { data, unitLen, audioType ->
        Log.i(TAG, "我是原始音频数据回调")
    }

    var encVideoStreamCallback = EncVideoStreamCallback { data, unitLen, audioType ->
        Log.d(TAG, "我是原始视频数据回调")
    }

    private val mRenderCallback: RenderCallback = object : RenderCallback {
        /*
         * status为STT_DC_KEYFRM_FETCHED表示视频已经开始播放，重写该函数可以做一些额外的处理工作，
         * 比如隐藏ProgressDialog等
         */
        override fun onStatusFetched(res: ClientRes, status: Byte, errorCode: Int): Int {
            Log.d(TAG, "onStatusFetched, status = $status")
            if (status == NetStatusCallback.STT_DC_FRM_FETCHED || status == NetStatusCallback.STT_DC_KEYFRM_FETCHED) {
                Log.d(TAG, "视频预览中...")
                if (status == NetStatusCallback.STT_DC_KEYFRM_FETCHED) {
                    /* 该标志表示出现画面 */
                    Log.d(TAG, "视频开始播放...")
                }
                val size = IntArray(2)
                var width = 0
                var height = 0
                if (res is InputVideo) {
                    res.getSize(size)
                    width = size[0]
                    height = size[1]
                }
                mRenderView.setResName(res.puid(), res.resIndex, width, height)
            }
            return 0
        }

        override fun onProcessFetched(percent: Int) {
            Log.d(TAG, " 当前已播放比例=$percent")
            if (percent == 100) {
                Log.d(TAG, "视频播放完毕")
            }
        }
    }

    private fun startTimer() {
        if (timer == null) {
            timer = Timer()
        }
        if (timerTask == null) {
            timerTask = object : TimerTask() {
                override fun run() {
                    if (mRenderView != null) {
                        mRenderView.stopRecord()
                    }
                    finish()
                }
            }
            timer?.schedule(timerTask, 3000)
        }
    }

    private fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
    }
    private var checkDuiJiang:Boolean = true
    private var checkMianTi:Boolean = true
    override fun onClick(v: View?) {
        when (v) {
            ivBack -> finish()
            btnDuijiang ->dujiang()
            btnMianti -> mianti()
        }

    }

    fun dujiang(){
        client!!.handleTalk()
        checkDuiJiang = if (checkDuiJiang) {
            AudioResManager.getInstance().setPttPressed(true)
            btnDuijiang.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.icon_duijiangji_down), null, null)
            false
        } else {
            AudioResManager.getInstance().setPttPressed(false)
            btnDuijiang.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.icon_duijiangji_up), null, null)
            true
        }
    }
    fun mianti(){
        client!!.handlePlay()
        checkMianTi = if (checkMianTi) {
            btnMianti.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.icon_speaker_down), null, null)
            false
        } else {
            btnMianti.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.icon_speaker_up), null, null)
            true
        }
    }
}