package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CrearTest : AppCompatActivity() {
    private var preguntas = mutableListOf<Pregunta>()

    data class Pregunta(
        val enunciado: String,
        val tipo: String,
        val opciones: List<Pair<String, Boolean>>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_test)

        SingletonMap["lista_preguntas"] = preguntas
        findViewById<FloatingActionButton>(R.id.addQuestion).setOnClickListener {
            val intent = Intent(this, SeleccionarTipoDePregunta::class.java)
            startActivity(intent)
        }


        val tags = findViewById<MultiAutoCompleteTextView>(R.id.multiAutoCompleteTextView)

        val lista_tags = listOf("entretenimiento", "cultura", "ciencias", "matemáticas", "historia", "universidad", "animales", "arte", "fiestas", "deportes", "geografía", "juegos", "marcas", "música")

        ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista_tags).also { adapter ->
            tags.setAdapter(adapter)
        }
        tags.setTokenizer(CommaTokenizer())
        tags.threshold = 0

        findViewById<Button>(R.id.cancelar).setOnClickListener {
            this.finish()
        }

        findViewById<Button>(R.id.crear).setOnClickListener {
            val titulo = findViewById<EditText>(R.id.titulo).text
            val descripcion = findViewById<EditText>(R.id.descripcion).text
            val tag_list = tags.text.toString().split(", ").filter { it.isNotEmpty() }.distinct()
            if (preguntas.size < 3) {
                Toast.makeText(
                    this,
                    getString(R.string.test_minimo_3_preguntas),
                    Toast.LENGTH_SHORT
                ).show()

            } else if (tag_list.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.test_minimo_1_categoria),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (titulo.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.test_minimo_1_titulo),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (descripcion.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.test_minimo_1_descripcion),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val test = hashMapOf(
                    "categorias" to tag_list,
                    "descripcion" to descripcion.toString(),
                    "titulo" to titulo.toString(),
                    "fecha de creacion" to Timestamp.now(),
                    "preguntas" to preguntas.map {
                        hashMapOf(
                            "enunciado" to it.enunciado,
                            "tipo" to it.tipo,
                            "opciones" to it.opciones.map { opcion ->
                                hashMapOf(
                                    "respuesta" to opcion.first,
                                    "correcta" to opcion.second
                                )
                            }
                        )
                    }
                )
                val new_test = Firebase.firestore.collection("tests")
                    .document()
                new_test.set(test)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            getString(R.string.test_creado_exito),
                            Toast.LENGTH_SHORT
                        ).show()
                        this.finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            getString(R.string.test_creado_fallo) + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                val doc_id = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                val assign_to_user = Firebase.firestore.collection("usuarios").document(doc_id)
                assign_to_user.update("mis tests", FieldValue.arrayUnion(new_test))
                    .addOnSuccessListener {  }
                    .addOnFailureListener { exception -> Toast.makeText(
                        this,
                        getString(R.string.test_asignado_fallo) + "${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show() }
            }


        }
    }

    fun actualizar_recicler_view() {
        val recyclerView = findViewById<RecyclerView>(R.id.listaPreguntas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomAdapter(preguntas)
    }

    // usa esta funcion para actualizar la lista cuando se añadan nuevos elementos?
    override fun onResume() {
        super.onResume()
        actualizar_recicler_view()
    }
    // Esto es del recycler view
    inner class CustomAdapter(private val dataSet: List<Pregunta>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tipo: TextView = view.findViewById(R.id.tipo)
            val enunciado: TextView = view.findViewById(R.id.enunciado)
            val view: View = view
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
            viewHolder.view.findViewById<Button>(R.id.boton_x).setOnClickListener {
                (SingletonMap["lista_preguntas"] as MutableList<Pregunta>).removeAt(position)
                actualizar_recicler_view()
            }
            viewHolder.view.findViewById<LinearLayout>(R.id.layout_pregunta).setOnClickListener {

                val intent = when (dataSet[position].tipo) {
                    "seleccion" -> Intent(viewHolder.enunciado.context, CrearPreguntaSeleccion::class.java)
                    "multiple" -> Intent(viewHolder.enunciado.context, CrearPreguntaMultipleRespuesta::class.java)
                    "rellenar huecos" -> Intent(viewHolder.enunciado.context, CrearPreguntaRellenarHuecos::class.java)
                    else -> throw Exception("Que cojones?")
                }

                intent.putExtra("idx", position)
                startActivity(intent)
            }
        }

        override fun getItemCount() = dataSet.size
    }


}