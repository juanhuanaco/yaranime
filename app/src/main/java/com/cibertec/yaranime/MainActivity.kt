package com.cibertec.yaranime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class MainActivity : AppCompatActivity() {

    private var thisObject = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timer = object : CountDownTimer(2000, 1000){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                startActivity(Intent(thisObject, HomeActivity::class.java))
            }

        }
        timer.start()
    }
}