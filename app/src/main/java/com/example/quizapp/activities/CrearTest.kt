package com.example.quizapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
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
    data class Pregunta(
        val enunciado: String,
        val tipo: String,
        val opciones: List<Pair<String, Boolean>>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_test)

        //Al pulsar el boton + se abre una nueva actividad para seleccionar el tipo de pregunta
        findViewById<FloatingActionButton>(R.id.addQuestion).setOnClickListener {
            val intent = Intent(this, SeleccionarTipoDePregunta::class.java)
            startActivity(intent)
        }

        val tags = findViewById<MultiAutoCompleteTextView>(R.id.multiAutoCompleteTextView)

        //Lista de etiquetas posibles para un test
        val lista_tags = listOf(
            "entretenimiento",
            "cultura",
            "ciencias",
            "matemáticas",
            "historia",
            "universidad",
            "animales",
            "arte",
            "fiestas",
            "deportes",
            "geografía",
            "juegos",
            "marcas",
            "música"
        )
        //La lista se convierte en un objeto de tipo ArrayAdapter y ademas se adapta al objeto creado
        ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            lista_tags
        ).also { adapter ->
            tags.setAdapter(adapter)
        }
        tags.setTokenizer(CommaTokenizer())
        tags.threshold = 0

        val extras = intent.extras
        if (extras != null) {
            //Si se esta editando el test, se debe mostrar la informacion relevante del mismo como
                // las categorias, el titulo, la descripcion, las categorias y las preguntas que tuviera
            Firebase.firestore.collection("tests")
                .document(extras.getString("id")!!).get().addOnSuccessListener { test ->
                    tags.setText((test["categorias"] as List<String>).joinToString(", ", postfix = ", "), TextView.BufferType.EDITABLE)
                    findViewById<EditText>(R.id.titulo).setText(test["titulo"] as String, TextView.BufferType.EDITABLE)
                    findViewById<EditText>(R.id.descripcion).setText(test["descripcion"] as String, TextView.BufferType.EDITABLE)
                    SingletonMap["lista_preguntas"] = (test["preguntas"] as List<HashMap<String,*>>).map { pregunta ->
                        Pregunta(pregunta["enunciado"] as String, pregunta["tipo"] as String, (pregunta["opciones"] as List<HashMap<String, *>>).map { opcion ->
                            Pair(opcion["respuesta"] as String, opcion["correcta"] as Boolean)
                        })
                    } as MutableList<Pregunta>
                    actualizar_recicler_view()
                }
        } else {
            //En caso de estar creando un nuevo test, se inicializa la lista de preguntas como vacia
            SingletonMap["lista_preguntas"] = mutableListOf<Pregunta>()
        }
        //Si el usuario cancela la creacion del test, se cierra la actividad actual
        findViewById<Button>(R.id.cancelar).setOnClickListener {
            this.finish()
        }

        //Codigo necesario para mostrar un AlertDialog
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                    }
                }
            }

        val builder = AlertDialog.Builder(this)

        //Al pulsar sobre el boton crear test se comprueba que este cumpla los requisitos minimos
        //para ser creado: tener un titulo, una descripcion, al menos una categoria y al menos 3
        //preguntas. En caso de no cumplir alguno de estos requisitos, se muestra el correspondiente
        //alertDialog informando del incumplimiento. Una vez se satisfacen los requisitos, se crea el
        //test y se inserta un nuevo test en la BD si se esta creando o se modifica el actual si se
        //estaba editando
        findViewById<Button>(R.id.crear).setOnClickListener {
            val titulo = findViewById<EditText>(R.id.titulo).text
            val descripcion = findViewById<EditText>(R.id.descripcion).text
            val tag_list = tags.text.toString().split(", ").filter { it.isNotEmpty() }.distinct()
            val preguntas = SingletonMap["lista_preguntas"] as MutableList<Pregunta>
            if (titulo.isEmpty()) {
                builder.setMessage(getString(R.string.test_minimo_1_titulo)).setPositiveButton("OK", dialogClickListener).show()
            } else if (descripcion.isEmpty()) {
                builder.setMessage(getString(R.string.test_minimo_1_descripcion)).setPositiveButton("OK", dialogClickListener).show()
            } else if (tag_list.isEmpty()) {
                builder.setMessage(getString(R.string.test_minimo_1_categoria)).setPositiveButton("OK", dialogClickListener).show()
            } else if (preguntas.size < 3) {
                builder.setMessage(getString(R.string.test_minimo_3_preguntas)).setPositiveButton("OK", dialogClickListener).show()
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
                    },
                    "valoracion" to 5.0
                )
                //En caso de estar editando el test (extras es diferente de null), se obtiene de la
                //BD dado su id y en otro caso se inserta el nuevo test en la BD
                val new_test = if (extras != null) {
                    Firebase.firestore.collection("tests")
                        .document(extras.getString("id")!!)
                } else {
                    Firebase.firestore.collection("tests")
                        .document()
                }
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
                        builder.setMessage(getString(R.string.test_creado_fallo) + it.message).setPositiveButton("OK", dialogClickListener).show()
                    }
                //Una vez insertado o modificado el test en la BD, este se añade a la lista de tests
                //de un usuario. En caso de fallo, se muestra un mensaje indicando que ha fallado
                val doc_id = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                val assign_to_user = Firebase.firestore.collection("usuarios").document(doc_id)
                assign_to_user.update("mis tests", FieldValue.arrayUnion(new_test))
                    .addOnSuccessListener { }
                    .addOnFailureListener { exception ->
                        builder.setMessage(getString(R.string.test_asignado_fallo) + "${exception.message}").setPositiveButton("OK", dialogClickListener).show()
                    }
            }


        }
    }

    //Funcion para actualizar el recycler view cada vez que se añade una nueva pregunta al test
    fun actualizar_recicler_view() {
        val recyclerView = findViewById<RecyclerView>(R.id.listaPreguntas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomAdapter(SingletonMap["lista_preguntas"] as MutableList<Pregunta>)
    }

    override fun onResume() {
        super.onResume()
        actualizar_recicler_view()
    }

    inner class CustomAdapter(private val dataSet: List<Pregunta>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val tipo: TextView = view.findViewById(R.id.tipo)
            val enunciado: TextView = view.findViewById(R.id.enunciado)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.preview_pregunta, viewGroup, false)
            return ViewHolder(view)
        }

        //Cuando se muestran las preguntas del test que se esta creando, se muestran el tipo y el enunciado
        //de la pregunta. Para cada una de las preguntas se ejecuta la siguiente funcion. Tambien es posible
        //eliminar las preguntas del test si se pulsa el boton X y se refresca el recycler view
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            var tipoTraducido = ""
            if(dataSet[position].tipo.equals("rellenar huecos")){
                tipoTraducido = getString(R.string.tipo_rellenar)
            } else if (dataSet[position].tipo.equals("seleccion")){
                tipoTraducido = getString(R.string.tipo_seleccion)
            } else if (dataSet[position].tipo.equals("multiple")){
                tipoTraducido = getString(R.string.tipo_multiple)
            }
            viewHolder.tipo.text = tipoTraducido
            viewHolder.enunciado.text = dataSet[position].enunciado
            viewHolder.view.findViewById<Button>(R.id.boton_x).setOnClickListener {
                (SingletonMap["lista_preguntas"] as MutableList<Pregunta>).removeAt(position)
                actualizar_recicler_view()
            }
            viewHolder.view.findViewById<LinearLayout>(R.id.layout_pregunta).setOnClickListener {
                //Al pulsar sobre alguna de las preguntas es posible editarla. Segun el tipo de pregunta
                //se abrira la actividad correspondiente, pasando el indice de la pregunta a la nueva
                //actividad.
                val intent = when (dataSet[position].tipo) {
                    "seleccion" -> Intent(
                        viewHolder.enunciado.context,
                        CrearPreguntaSeleccion::class.java
                    )
                    "multiple" -> Intent(
                        viewHolder.enunciado.context,
                        CrearPreguntaMultipleRespuesta::class.java
                    )
                    "rellenar huecos" -> Intent(
                        viewHolder.enunciado.context,
                        CrearPreguntaRellenarHuecos::class.java
                    )
                    else -> throw Exception("Program crashed")
                }

                intent.putExtra("idx", position)
                startActivity(intent)
            }
        }

        override fun getItemCount() = dataSet.size
    }


}