package com.ahhwxx.samplemcu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_p_u_i_d.*

class PUIDActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_u_i_d)
        btn_play.setOnClickListener{
            startActivity(Intent(this@PUIDActivity,PlayVideoActitity::class.java))
            finish()
        }
    }
}