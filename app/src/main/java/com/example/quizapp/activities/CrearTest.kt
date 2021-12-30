package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CrearTest : AppCompatActivity() {
    var preguntas = mutableListOf<Pregunta>()
    data class Pregunta(val enunciado : String, val tipo : String, val opciones : List<Pair<String, Boolean>>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_test)

        SingletonMap["lista_preguntas"] = preguntas

        findViewById<FloatingActionButton>(R.id.addQuestion).setOnClickListener { view ->
            val intent = Intent(this, SeleccionarTipoDePregunta::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.cancelar).setOnClickListener { view ->
            this.finish()
        }

        findViewById<Button>(R.id.crear).setOnClickListener { view ->

            val titulo = findViewById<EditText>(R.id.titulo).text
            val descripcion = findViewById<EditText>(R.id.descripcion).text

            val test = hashMapOf(
                "categoria" to "TODO hacer esto",
                "descripcion" to descripcion.toString(),
                "titulo" to titulo.toString(),
                "fecha de creacion" to Timestamp.now(),
                "preguntas" to preguntas.map { hashMapOf(
                    "enunciado" to it.enunciado,
                    "tipo" to it.tipo,
                    "opciones" to it.opciones.map { hashMapOf(
                        "respuesta" to it.first,
                        "correcta" to it.second
                    ) }
                ) }
            )
            Firebase.firestore.collection("tests")
                .document().set(test)
                .addOnSuccessListener { Toast.makeText(
                    this,
                    "Se ha creado el test correctamente",
                    Toast.LENGTH_SHORT
                ).show() }
                .addOnFailureListener { Toast.makeText(
                    this,
                    "No se ha podido guardar información sobre el ususario: " + it.message,
                    Toast.LENGTH_SHORT
                ).show() }

        }
    }

    // usa esta funcion para actualizar la lista cuando se añadan nuevos elementos?
    override fun onResume() {
        super.onResume()
        val recyclerView = findViewById<RecyclerView>(R.id.listaPreguntas);
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomAdapter(preguntas)
    }

    // Esto es del recycler view
    inner class CustomAdapter(private val dataSet: List<Pregunta>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tipo: TextView
            val enunciado: TextView

            init {
                // Define click listener for the ViewHolder's View.
                tipo = view.findViewById(R.id.tipo)
                enunciado = view.findViewById(R.id.enunciado)
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.preview_pregunta, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.tipo.text = dataSet[position].tipo
            viewHolder.enunciado.text = dataSet[position].enunciado
        }

        override fun getItemCount() = dataSet.size
    }

}