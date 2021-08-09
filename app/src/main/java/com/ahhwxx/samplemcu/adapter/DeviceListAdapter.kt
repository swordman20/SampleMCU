package com.ahhwxx.samplemcu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ahhwxx.samplemcu.R
import com.oem.sdk.res.ClientNode
import com.oem.sdk.res.DomainNode
import com.oem.sdk.res.PeerUnit

/**
 *    author : hades
 *    date   : 8/9/21
 *    desc   :设备列表
 */
class DeviceListAdapter () : BaseAdapter() {
    var context: Context? = null
    var items: List<ClientNode>? = null

    constructor(arg1: Context, root: ClientNode) : this() {
        this.context = arg1
        if (root is DomainNode) {
            // 如果是域的话，列出域下面的设备资源，首次获取平台资源列表
            items = root.getEncoder(false)
        } else if (root is PeerUnit) {
            // 如果是设备的话，列出设备下面的摄像头资源
            items = root.getChildren(ClientNode.IV)
        }
    }

    override fun getCount(): Int {
        return items!!.size
    }

    override fun getItem(arg0: Int): Any {
        return items!![arg0]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.clientnode, parent, false)
        }
        val title = convertView!!.findViewById<View>(R.id.device) as TextView
        val node = getItem(position) as ClientNode
        if (node.online()) {
            title.text = "$node(在线)"
        } else {
            title.text = "$node(离线)"
        }
        return convertView
    }
}