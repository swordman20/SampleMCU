package com.ahhwxx.samplemcu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahhwxx.samplemcu.adapter.DeviceListAdapter
import com.oem.sdk.base.GlobalHelper
import com.oem.sdk.res.ClientNode
import com.oem.sdk.res.DomainNode
import com.oem.sdk.res.InputVideo
import com.oem.sdk.res.PeerUnit
import java.util.*
import kotlinx.android.synthetic.main.activity_device_list.*

/**
 *    author : hades
 *    date   : 8/9/21
 *    desc   :设备列表
 */
class DeviceListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        initData()
    }

    private fun initData() {
        val root = DomainNode.getInstance1()
        val nRet = root.getPeerUnits(GlobalHelper.getClient())
        if (nRet == 0) {
            runOnUiThread(Runnable { initList(root) })
        }
    }

    // 当前资源树的根节点
    var mRoot: ClientNode? = null

    // 用资源初始化list控件
    fun initList(root: ClientNode?) {
        mRoot = root
        if (root != null) {
            initListViewWithClientNode(mList, root, true)
        }
        mList.onItemClickListener = this@DeviceListActivity
    }

    private fun initListViewWithClientNode(list: ListView, root: ClientNode,
                                           sort: Boolean): Int {
        if (sort) { // 需要排序
            root.children().sort()
        }
        if (list.adapter == null) {
            val view = LayoutInflater.from(this).inflate(R.layout.rootnode,
                    null)
            val tView = view.findViewById<View>(R.id.device) as TextView
            tView.text = root.name()
            list.addHeaderView(view)
        } else {
            val view = list.getChildAt(0)
            val tView = view.findViewById<View>(R.id.device) as TextView
            tView.text = root.name()
        }
        val adapter = DeviceListAdapter(this, root)
        list.adapter = adapter
        adapter.notifyDataSetChanged()
        return adapter.count
    } // 连接状态回调


    override fun onItemClick(arg0: AdapterView<*>, view: View?, position: Int,
                             arg3: Long) {
        arg0.isEnabled = false
        // 点击根节点，返回上一层
        if (position == 0) {
            if (mRoot != null && mRoot!!.parent() != null) {
                initList(mRoot!!.parent())
            }
            arg0.setEnabled(true)
        } else {
            val node = arg0.adapter.getItem(position) as ClientNode
            // 如果是域节点的话，获取的是域下的设备节点
            if (node is DomainNode) {
                //Assert.assertTrue(!domainNode.isThisDomain());
                object : Thread() {
                    override fun run() {
                        super.run()
                        val nRet = node.getPeerUnits(GlobalHelper
                                .getClient())
                        runOnUiThread(Runnable {
                            if (nRet == 0) {
                                initList(node)
                            }
                            arg0.isEnabled = true
                        })
                    }
                }.start()
            } else if (node is PeerUnit) {
                // 如果是设备节点，获取的是设备下的视频资源
                val isFixedPoint = node.isFixedPoint
                if (isFixedPoint) {
                    Toast.makeText(
                            this, String.format("设备的经纬度为：(%f,%f)",
                            node.longitude(), node.latitude()),
                            Toast.LENGTH_SHORT).show()
                }
                object : Thread() {
                    override fun run() {
                        super.run()
                        val nRet = node.getPeerUnitRes(GlobalHelper
                                .getClient())
                        runOnUiThread(Runnable {
                            if (nRet == 0) {
                                initList(node)
                            }
                            arg0.isEnabled = true
                        })
                    }
                }.start()
            } else if (node is InputVideo) {
                // 如果是视频节点，则播放视频
                val puid = node.puid()
                startPlay(puid)

//                rendIV(iv);
                arg0.isEnabled = true
            }
        }
    }

    private fun startPlay(puid: String) {
        val intent = Intent(this, PlayVideoActitity::class.java)
        intent.putExtra("puid", puid)
        startActivity(intent)
    }
}