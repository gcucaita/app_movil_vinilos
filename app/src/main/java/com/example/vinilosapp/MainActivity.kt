package com.example.vinilosapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vinilosapp.data.repository.AlbumRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val albumRepository = AlbumRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val statusText = findViewById<TextView>(R.id.statusText)

        lifecycleScope.launch {
            val albums = albumRepository.getAllAlbums()

            if (albums != null) {
                val firstAlbumName = albums.firstOrNull()?.name ?: "Sin albums"
                statusText.text = "Albums cargados: ${albums.size}\nPrimero: $firstAlbumName"
                Toast.makeText(this@MainActivity, "Se cargaron ${albums.size} albums", Toast.LENGTH_SHORT).show()
            } else {
                statusText.text = "No se pudieron cargar los albums"
                Toast.makeText(this@MainActivity, "Error cargando albums", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
