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
    private lateinit var btAtualizarPerfil: Button
    private lateinit var btVoltarMenu: Button
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

        btAtualizarPerfil.setOnClickListener {
            atualizarPerfil()
        }

        btVoltarMenu.setOnClickListener {
            finish()
        }

        btSair.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, FormLogin::class.java))
            finish()
        }
    }

    private fun iniciarComponentes() {
        nomeUser = findViewById(R.id.textNomeUser)
        emailUser = findViewById(R.id.textEmailUser)
        btAtualizarPerfil = findViewById(R.id.bt_atualizar_perfil)
        btVoltarMenu = findViewById(R.id.bt_voltar_menu_perfil)
        btSair = findViewById(R.id.bt_sair)
    }

    override fun onStart() {
        super.onStart()
        val usuarioAtual = auth.currentUser
        if (usuarioAtual == null) {
            startActivity(Intent(this, FormLogin::class.java))
            finish()
        } else {
            buscarDadosUsuario(usuarioAtual.uid)
        }
    }

    private fun buscarDadosUsuario(uid: String) {
        db.collection("Usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    nomeUser.setText(documento.getString("nome") ?: "")
                    emailUser.setText(documento.getString("email") ?: auth.currentUser?.email.orEmpty())
                } else {
                    emailUser.setText(auth.currentUser?.email.orEmpty())
                    Toast.makeText(this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao buscar dados: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarPerfil() {
        val usuarioAtual = auth.currentUser
        val nome = nomeUser.text.toString().trim()

        if (usuarioAtual == null) {
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        if (nome.isEmpty()) {
            nomeUser.error = "Digite seu nome"
            return
        }

        db.collection("Usuarios")
            .document(usuarioAtual.uid)
            .update("nome", nome)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao atualizar perfil: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }
}
