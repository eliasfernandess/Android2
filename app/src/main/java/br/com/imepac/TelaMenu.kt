package br.com.imepac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TelaMenu : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textUsuario: TextView
    private lateinit var btPerfil: Button
    private lateinit var btCadastrarTarefa: Button
    private lateinit var btBuscarTarefa: Button
    private lateinit var btListarTarefas: Button
    private lateinit var btSobre: Button
    private lateinit var btSair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_menu)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        iniciarComponentes()

        val email = auth.currentUser?.email ?: "Usuário logado"
        textUsuario.text = email

        btPerfil.setOnClickListener {
            startActivity(Intent(this, TelaPerfil::class.java))
        }

        btCadastrarTarefa.setOnClickListener {
            startActivity(Intent(this, TelaCadastroTarefa::class.java))
        }

        btBuscarTarefa.setOnClickListener {
            startActivity(Intent(this, TelaBuscarTarefa::class.java))
        }

        btListarTarefas.setOnClickListener {
            startActivity(Intent(this, TelaListaTarefasPaginada::class.java))
        }

        btSobre.setOnClickListener {
            startActivity(Intent(this, TelaSobre::class.java))
        }

        btSair.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, FormLogin::class.java))
            finish()
        }
    }

    private fun iniciarComponentes() {
        textUsuario = findViewById(R.id.text_usuario_menu)
        btPerfil = findViewById(R.id.bt_menu_perfil)
        btCadastrarTarefa = findViewById(R.id.bt_menu_cadastrar_tarefa)
        btBuscarTarefa = findViewById(R.id.bt_menu_buscar_tarefa)
        btListarTarefas = findViewById(R.id.bt_menu_listar_tarefas)
        btSobre = findViewById(R.id.bt_menu_sobre)
        btSair = findViewById(R.id.bt_menu_sair)
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, FormLogin::class.java))
            finish()
        }
    }
}
