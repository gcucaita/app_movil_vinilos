package com.example.vinilosapp.ui.base

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.vinilosapp.MainActivity
import com.example.vinilosapp.R
import com.example.vinilosapp.ui.collectors.CollectorListActivity
import com.example.vinilosapp.ui.musicians.MusicianListActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
abstract class BaseActivity : AppCompatActivity() {
    private fun applyWindowInsets() {
        val root = window.decorView.findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
    protected fun setupBottomNav(activeTab: Int) {
        val bottomNav = findViewById<View>(R.id.bottomNav)

        val navAlbums = bottomNav.findViewById<TextView>(R.id.navAlbums)
        val navArtists = bottomNav.findViewById<TextView>(R.id.navArtists)
        val navCollectors = bottomNav.findViewById<TextView>(R.id.navCollectors)

        val active = ContextCompat.getColor(this, R.color.green_accent)
        val inactive = ContextCompat.getColor(this, R.color.nav_text)

        navAlbums.setTextColor(if (activeTab == 0) active else inactive)
        navArtists.setTextColor(if (activeTab == 1) active else inactive)
        navCollectors.setTextColor(if (activeTab == 2) active else inactive)

        navAlbums.contentDescription = if (activeTab == 0) "Álbumes, seleccionado" else "Ir a Álbumes"
        navArtists.contentDescription = if (activeTab == 1) "Artistas, seleccionado" else "Ir a Artistas"
        navCollectors.contentDescription = if (activeTab == 2) "Coleccionistas, seleccionado" else "Ir a Coleccionistas"

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