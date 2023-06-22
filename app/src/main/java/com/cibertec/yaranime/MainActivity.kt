package com.cibertec.yaranime

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainActivity : AppCompatActivity() {

    private var thisObject = this
    lateinit var anime:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyDlap2eVFZwpt0dzkoVpwVd5If5VeVnw-g")
            .setApplicationId("1:494057504345:android:d3f3d3cb77509bc396fb48")
            .setDatabaseUrl("https://yaranime-eventos-default-rtdb.firebaseio.com/")
            .build()

        try {
            FirebaseApp.initializeApp(applicationContext, options, "events-database")
        } catch (e:Exception){}

        anime = findViewById(R.id.anime)
        Glide.with(this).load("https://www.icegif.com/wp-content/uploads/icegif-2013.gif").into(anime)
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