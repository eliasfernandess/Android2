package br.com.imepac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class FormLogin : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btEntrar: Button
    private lateinit var textTelaCadastro: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_login)
        supportActionBar?.hide()

        iniciarComponentes()
        auth = FirebaseAuth.getInstance()

        textTelaCadastro.setOnClickListener {
            startActivity(Intent(this, FormCadastro::class.java))
        }

        btEntrar.setOnClickListener {
            fazerLogin()
        }
    }

    private fun iniciarComponentes() {
        editEmail = findViewById(R.id.edit_email_login)
        editSenha = findViewById(R.id.edit_senha_login)
        btEntrar = findViewById(R.id.bt_entrar)
        textTelaCadastro = findViewById(R.id.text_tela_cadastro)
        progressBar = findViewById(R.id.progressbar_login)
    }

    private fun fazerLogin() {
        val email = editEmail.text.toString().trim()
        val senha = editSenha.text.toString().trim()

        if (email.isEmpty()) {
            editEmail.error = "Digite seu e-mail"
            return
        }

        if (senha.isEmpty()) {
            editSenha.error = "Digite sua senha"
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    startActivity(Intent(this, TelaMenu::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao fazer login: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        if (usuarioAtual != null) {
            startActivity(Intent(this, TelaMenu::class.java))
            finish()
        }
    }
}
