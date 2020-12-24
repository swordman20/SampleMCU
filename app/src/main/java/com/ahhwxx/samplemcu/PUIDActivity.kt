package com.ahhwxx.samplemcu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_p_u_i_d.*

class PUIDActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_u_i_d)
        btn_play.setOnClickListener{
            var intent:Intent = Intent(this@PUIDActivity,PlayVideoActitity::class.java)
            val puid = et_puid.text.toString().trim()
            if (!TextUtils.isEmpty(puid)) {
                intent.putExtra("puid",et_puid.text.toString().trim())
                startActivity(intent)
            }

        }
    }
}