package com.cibertec.yaranime

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cibertec.yaranime.fragments.ConfigFragment
import com.cibertec.yaranime.fragments.EventFragment
import com.cibertec.yaranime.fragments.MusicDirectoryFragment
import com.cibertec.yaranime.fragments.MusicFragment
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    lateinit var frameLayout: FrameLayout
    lateinit var tabLayout: TabLayout
    var musicView = MusicDirectoryFragment()
    var eventView = EventFragment()
    var configView = ConfigFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        frameLayout = findViewById(R.id.frameLayout)
        tabLayout = findViewById(R.id.tabLayout)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, eventView)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()



        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab ){
                val fragment : Fragment =
                when (tab.position){
                    0 -> eventView
                    1 -> musicView
                    2 -> configView
                    else -> eventView
                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }
        })
    }



}