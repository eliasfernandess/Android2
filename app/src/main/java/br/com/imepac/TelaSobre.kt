package br.com.imepac

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TelaSobre : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_sobre)
        supportActionBar?.hide()

        val btVoltar = findViewById<Button>(R.id.bt_voltar_sobre)
        btVoltar.setOnClickListener {
            finish()
        }
    }
}
