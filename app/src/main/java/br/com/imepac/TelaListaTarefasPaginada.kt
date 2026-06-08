package br.com.imepac

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TelaListaTarefasPaginada : AppCompatActivity() {

    private lateinit var tabelaTarefas: TableLayout
    private lateinit var textPagina: TextView
    private lateinit var btRecarregar: Button
    private lateinit var btProximaPagina: Button
    private lateinit var btVoltar: Button
    private lateinit var db: FirebaseFirestore
    private var ultimoDocumento: DocumentSnapshot? = null
    private var paginaAtual = 1

    companion object {
        private const val TAMANHO_PAGINA = 5L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_lista_tarefas_paginada)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        iniciarComponentes()

        btRecarregar.setOnClickListener {
            carregarTarefas(resetar = true)
        }

        btProximaPagina.setOnClickListener {
            carregarTarefas(resetar = false)
        }

        btVoltar.setOnClickListener {
            finish()
        }

        carregarTarefas(resetar = true)
    }

    private fun iniciarComponentes() {
        tabelaTarefas = findViewById(R.id.tabela_tarefas_paginada)
        textPagina = findViewById(R.id.text_pagina_atual)
        btRecarregar = findViewById(R.id.bt_recarregar_lista)
        btProximaPagina = findViewById(R.id.bt_proxima_pagina)
        btVoltar = findViewById(R.id.bt_voltar_lista)
    }

    private fun carregarTarefas(resetar: Boolean) {
        if (resetar) {
            ultimoDocumento = null
            paginaAtual = 1
        }

        val numeroDaPagina = if (resetar) 1 else paginaAtual + 1

        var consulta: Query = db.collection("Tarefas")
            .orderBy("criadoEm", Query.Direction.DESCENDING)
            .limit(TAMANHO_PAGINA)

        val documentoInicial = ultimoDocumento
        if (!resetar && documentoInicial != null) {
            consulta = consulta.startAfter(documentoInicial)
        }

        consulta.get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) {
                    Toast.makeText(this, "Não há mais registros nesta página", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                tabelaTarefas.removeAllViews()
                montarCabecalho()

                for (documento in resultado.documents) {
                    adicionarTarefaNaTabela(
                        documento.getString("codigo") ?: "",
                        documento.getString("titulo") ?: "",
                        documento.getString("status") ?: "",
                        documento.getString("dataEntrega") ?: ""
                    )
                }

                ultimoDocumento = resultado.documents.last()
                paginaAtual = numeroDaPagina
                textPagina.text = "Página $paginaAtual - ${resultado.size()} registro(s)"
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao listar: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun montarCabecalho() {
        val linha = TableRow(this)
        linha.addView(criarCelula("Código", true))
        linha.addView(criarCelula("Título", true))
        linha.addView(criarCelula("Status", true))
        linha.addView(criarCelula("Entrega", true))
        tabelaTarefas.addView(linha)
    }

    private fun adicionarTarefaNaTabela(codigo: String, titulo: String, status: String, dataEntrega: String) {
        val linha = TableRow(this)
        linha.addView(criarCelula(codigo, false))
        linha.addView(criarCelula(titulo, false))
        linha.addView(criarCelula(status, false))
        linha.addView(criarCelula(dataEntrega, false))
        tabelaTarefas.addView(linha)
    }

    private fun criarCelula(texto: String, negrito: Boolean): TextView {
        val textView = TextView(this)
        textView.text = texto
        textView.setTextColor(android.graphics.Color.WHITE)
        textView.textSize = 13f
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.minWidth = 170
        textView.setPadding(14, 12, 14, 12)
        if (negrito) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        }
        return textView
    }
}
