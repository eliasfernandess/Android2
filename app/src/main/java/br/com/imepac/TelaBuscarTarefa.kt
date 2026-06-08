package br.com.imepac

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class TelaBuscarTarefa : AppCompatActivity() {

    private lateinit var editCodigoBusca: EditText
    private lateinit var editTitulo: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editStatus: EditText
    private lateinit var editDataEntrega: EditText
    private lateinit var btBuscar: Button
    private lateinit var btAtualizar: Button
    private lateinit var btVoltar: Button
    private lateinit var tabelaResultado: TableLayout
    private lateinit var db: FirebaseFirestore
    private var documentoEncontrado: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_buscar_tarefa)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        iniciarComponentes()
        habilitarEdicao(false)

        btBuscar.setOnClickListener {
            buscarUmRegistro()
        }

        btAtualizar.setOnClickListener {
            atualizarRegistro()
        }

        btVoltar.setOnClickListener {
            finish()
        }
    }

    private fun iniciarComponentes() {
        editCodigoBusca = findViewById(R.id.edit_codigo_busca)
        editTitulo = findViewById(R.id.edit_titulo_update)
        editDescricao = findViewById(R.id.edit_descricao_update)
        editStatus = findViewById(R.id.edit_status_update)
        editDataEntrega = findViewById(R.id.edit_data_entrega_update)
        btBuscar = findViewById(R.id.bt_buscar_um_registro)
        btAtualizar = findViewById(R.id.bt_atualizar_registro)
        btVoltar = findViewById(R.id.bt_voltar_busca)
        tabelaResultado = findViewById(R.id.tabela_resultado_unico)
    }

    private fun buscarUmRegistro() {
        val codigo = editCodigoBusca.text.toString().trim()

        if (codigo.isEmpty()) {
            editCodigoBusca.error = "Digite o código"
            return
        }

        db.collection("Tarefas")
            .whereEqualTo("codigo", codigo)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) {
                    documentoEncontrado = null
                    limparResultado()
                    habilitarEdicao(false)
                    Toast.makeText(this, "Nenhuma tarefa encontrada", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val documento = resultado.documents[0]
                documentoEncontrado = documento
                preencherCampos(documento)
                montarTabela(documento)
                habilitarEdicao(true)
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao buscar: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun preencherCampos(documento: DocumentSnapshot) {
        editTitulo.setText(documento.getString("titulo") ?: "")
        editDescricao.setText(documento.getString("descricao") ?: "")
        editStatus.setText(documento.getString("status") ?: "")
        editDataEntrega.setText(documento.getString("dataEntrega") ?: "")
    }

    private fun montarTabela(documento: DocumentSnapshot) {
        tabelaResultado.removeAllViews()
        adicionarLinha("Código", documento.getString("codigo") ?: "", true)
        adicionarLinha("Título", documento.getString("titulo") ?: "", false)
        adicionarLinha("Descrição", documento.getString("descricao") ?: "", false)
        adicionarLinha("Status", documento.getString("status") ?: "", false)
        adicionarLinha("Data", documento.getString("dataEntrega") ?: "", false)
    }

    private fun adicionarLinha(campo: String, valor: String, destaque: Boolean) {
        val linha = TableRow(this)
        val campoView = criarCelula(campo, true)
        val valorView = criarCelula(valor, destaque)
        linha.addView(campoView)
        linha.addView(valorView)
        tabelaResultado.addView(linha)
    }

    private fun criarCelula(texto: String, negrito: Boolean): TextView {
        val textView = TextView(this)
        textView.text = texto
        textView.setTextColor(android.graphics.Color.WHITE)
        textView.textSize = 14f
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setPadding(16, 14, 16, 14)
        if (negrito) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        }
        return textView
    }

    private fun atualizarRegistro() {
        val documento = documentoEncontrado

        if (documento == null) {
            Toast.makeText(this, "Busque uma tarefa antes de atualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val titulo = editTitulo.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val status = editStatus.text.toString().trim()
        val dataEntrega = editDataEntrega.text.toString().trim()

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
            editDataEntrega.error = "Digite a data"
            return
        }

        val dadosAtualizados = hashMapOf<String, Any>(
            "titulo" to titulo,
            "descricao" to descricao,
            "status" to status,
            "dataEntrega" to dataEntrega,
            "atualizadoEm" to System.currentTimeMillis()
        )

        db.collection("Tarefas")
            .document(documento.id)
            .update(dadosAtualizados)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro atualizado com sucesso", Toast.LENGTH_SHORT).show()
                buscarUmRegistro()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao atualizar: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun limparResultado() {
        tabelaResultado.removeAllViews()
        editTitulo.text.clear()
        editDescricao.text.clear()
        editStatus.text.clear()
        editDataEntrega.text.clear()
    }

    private fun habilitarEdicao(habilitado: Boolean) {
        editTitulo.isEnabled = habilitado
        editDescricao.isEnabled = habilitado
        editStatus.isEnabled = habilitado
        editDataEntrega.isEnabled = habilitado
        btAtualizar.isEnabled = habilitado
    }
}
