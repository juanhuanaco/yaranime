package com.cibertec.yaranime

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.annotation.GlideType

class MainActivity : AppCompatActivity() {

    private var thisObject = this
    lateinit var anime:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        anime = findViewById(R.id.anime)
        var url = "https://www.icegif.com/wp-content/uploads/icegif-2013.gif"
        Uri.parse(url)
        Glide.with(this).load(url).into(anime)
        val timer = object : CountDownTimer(5000, 1000){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                startActivity(Intent(thisObject, HomeActivity::class.java))
            }

        }
        timer.start()
    }
}