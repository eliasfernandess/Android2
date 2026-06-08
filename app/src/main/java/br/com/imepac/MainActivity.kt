package br.com.imepac

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destino = if (FirebaseAuth.getInstance().currentUser == null) {
            FormLogin::class.java
        } else {
            TelaMenu::class.java
        }

        startActivity(Intent(this, destino))
        finish()
    }
}
