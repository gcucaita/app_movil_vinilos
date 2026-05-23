package com.example.vinilosapp.ui.base

import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vinilosapp.MainActivity
import com.example.vinilosapp.R
import com.example.vinilosapp.ui.collectors.CollectorListActivity
import com.example.vinilosapp.ui.musicians.MusicianListActivity

abstract class BaseActivity : AppCompatActivity() {

    protected fun setupBottomNav(activeTab: Int) {
        val navAlbums = findViewById<TextView>(R.id.navAlbums)
        val navArtists = findViewById<TextView>(R.id.navArtists)
        val navCollectors = findViewById<TextView>(R.id.navCollectors)

        // resalta el tab activo
        val active = getColor(R.color.green_accent)
        val inactive = getColor(R.color.nav_text)

        navAlbums.setTextColor(if (activeTab == 0) active else inactive)
        navArtists.setTextColor(if (activeTab == 1) active else inactive)
        navCollectors.setTextColor(if (activeTab == 2) active else inactive)

        navAlbums.setOnClickListener {
            if (activeTab != 0) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        navArtists.setOnClickListener {
            if (activeTab != 1) {
                startActivity(Intent(this, MusicianListActivity::class.java))
                finish()
            }
        }
        navCollectors.setOnClickListener {
            if (activeTab != 2) {
                startActivity(Intent(this, CollectorListActivity::class.java))
                finish()
            }
        }
    }
}