package br.com.imepac

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TelaCadastroTarefa : AppCompatActivity() {

    private lateinit var editCodigo: EditText
    private lateinit var editTitulo: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editStatus: EditText
    private lateinit var editDataEntrega: EditText
    private lateinit var btSalvar: Button
    private lateinit var btVoltar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_cadastro_tarefa)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        iniciarComponentes()

        btSalvar.setOnClickListener {
            cadastrarTarefa()
        }

        btVoltar.setOnClickListener {
            finish()
        }
    }

    private fun iniciarComponentes() {
        editCodigo = findViewById(R.id.edit_codigo_tarefa)
        editTitulo = findViewById(R.id.edit_titulo_tarefa)
        editDescricao = findViewById(R.id.edit_descricao_tarefa)
        editStatus = findViewById(R.id.edit_status_tarefa)
        editDataEntrega = findViewById(R.id.edit_data_entrega_tarefa)
        btSalvar = findViewById(R.id.bt_salvar_tarefa)
        btVoltar = findViewById(R.id.bt_voltar_cadastro_tarefa)
        progressBar = findViewById(R.id.progressbar_tarefa)
    }

    private fun cadastrarTarefa() {
        val codigo = editCodigo.text.toString().trim()
        val titulo = editTitulo.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val status = editStatus.text.toString().trim()
        val dataEntrega = editDataEntrega.text.toString().trim()
        val uid = auth.currentUser?.uid.orEmpty()

        if (codigo.isEmpty()) {
            editCodigo.error = "Digite um código"
            return
        }

        if (titulo.isEmpty()) {
            editTitulo.error = "Digite o título"
            return
        }

        if (descricao.isEmpty()) {
            editDescricao.error = "Digite a descrição"
            return
        }

        if (status.isEmpty()) {
            editStatus.error = "Digite o status"
            return
        }

        if (dataEntrega.isEmpty()) {
            editDataEntrega.error = "Digite a data de entrega"
            return
        }

        progressBar.visibility = View.VISIBLE

        db.collection("Tarefas")
            .whereEqualTo("codigo", codigo)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->
                if (!resultado.isEmpty) {
                    progressBar.visibility = View.GONE
                    editCodigo.error = "Já existe uma tarefa com esse código"
                    Toast.makeText(this, "Código já cadastrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val dados = hashMapOf(
                    "codigo" to codigo,
                    "titulo" to titulo,
                    "descricao" to descricao,
                    "status" to status,
                    "dataEntrega" to dataEntrega,
                    "usuarioId" to uid,
                    "criadoEm" to System.currentTimeMillis()
                )

                db.collection("Tarefas")
                    .add(dados)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE
                        limparCampos()
                        Toast.makeText(this, "Tarefa cadastrada com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { erro ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Erro ao cadastrar tarefa: ${erro.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { erro ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Erro ao validar código: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun limparCampos() {
        editCodigo.text.clear()
        editTitulo.text.clear()
        editDescricao.text.clear()
        editStatus.text.clear()
        editDataEntrega.text.clear()
        editCodigo.requestFocus()
    }
}
