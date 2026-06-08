package br.com.imepac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FormCadastro : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btCadastrar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_cadastro)
        supportActionBar?.hide()

        iniciarComponentes()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btCadastrar.setOnClickListener {
            cadastrarUsuario()
        }
    }

    private fun iniciarComponentes() {
        editNome = findViewById(R.id.edit_nome_cadastro)
        editEmail = findViewById(R.id.edit_email_cadastro)
        editSenha = findViewById(R.id.edit_senha_cadastro)
        btCadastrar = findViewById(R.id.bt_cadastrar)
        progressBar = findViewById(R.id.progressbar_cadastro)
    }

    private fun cadastrarUsuario() {
        val nome = editNome.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val senha = editSenha.text.toString().trim()

        if (nome.isEmpty()) {
            editNome.error = "Digite seu nome"
            return
        }

        if (email.isEmpty()) {
            editEmail.error = "Digite seu e-mail"
            return
        }

        if (senha.isEmpty()) {
            editSenha.error = "Digite sua senha"
            return
        }

        if (senha.length < 6) {
            editSenha.error = "A senha precisa ter no mínimo 6 caracteres"
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    salvarDadosUsuario(nome, email)
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Erro ao cadastrar: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun salvarDadosUsuario(nome: String, email: String) {
        val usuarioId = auth.currentUser?.uid

        if (usuarioId == null) {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Erro: usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val dadosUsuario = hashMapOf(
            "nome" to nome,
            "email" to email,
            "uid" to usuarioId,
            "criadoEm" to System.currentTimeMillis()
        )

        db.collection("Usuarios")
            .document(usuarioId)
            .set(dadosUsuario)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, TelaMenu::class.java))
                finish()
            }
            .addOnFailureListener { erro ->
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Erro ao salvar dados: ${erro.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
