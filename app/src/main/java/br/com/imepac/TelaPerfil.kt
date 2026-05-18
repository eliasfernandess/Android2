package br.com.imepac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TelaPerfil : AppCompatActivity() {

    private lateinit var nomeUser: EditText
    private lateinit var emailUser: EditText
    private lateinit var btSair: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_perfil)

        supportActionBar?.hide()

        iniciarComponentes()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btSair.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun iniciarComponentes() {
        nomeUser = findViewById(R.id.textNomeUser)
        emailUser = findViewById(R.id.textEmailUser)
        btSair = findViewById(R.id.bt_sair)
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        } else {
            val uid = usuarioAtual.uid
            buscarDadosUsuario(uid)
        }
    }

    private fun buscarDadosUsuario(uid: String) {
        db.collection("Usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {
                    val nome = documento.getString("nome")
                    val email = documento.getString("email")

                    nomeUser.setText(nome)
                    emailUser.setText(email)
                } else {
                    Toast.makeText(this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(
                    this,
                    "Erro ao buscar dados: ${erro.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}