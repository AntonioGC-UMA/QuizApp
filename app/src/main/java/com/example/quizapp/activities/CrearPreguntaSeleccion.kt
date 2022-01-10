package com.example.quizapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap

class CrearPreguntaSeleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_pregunta_seleccion)

        //Codigo necesario para poder mostrar un AlertDialog
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                    }
                }
            }

        val builder = AlertDialog.Builder(this)

        val texto = findViewById<EditText>(R.id.texto_seleccion)
        val radio_group = findViewById<RadioGroup>(R.id.radio_group)
        val enunciado = findViewById<EditText>(R.id.enunciado_seleccion)

        findViewById<Button>(R.id.nueva_seleccion).setOnClickListener {
            //Cuando se pulsa el boton nueva seleccion, se comprueba que el usuario haya introducido
            //una opcion para la pregunta
            if (texto.text.isEmpty()) {
                builder.setMessage(getString(R.string.texto_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
            } else {
                //Si ha introducido una opcion esta se añade a la lista de opciones que hubiera anteriormente
                val t = texto.text.toString()
                texto.text.clear()
                val r = RadioButton(this)
                r.text = t
                radio_group.addView(r)
            }
        }
        findViewById<Button>(R.id.cancelar_crear_pregunta_conectar).setOnClickListener {
            finish()
        }

        val extras = intent.extras

        if (extras != null) {
            //Si extras no es nulo, estamos editando la pregunta por lo que hay que mostrar la informacion
            // relevante de la misma como las opciones introducidas por el usuario anteriormente o el enunciado
            val value = extras.getInt("idx")
            val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
            enunciado.setText(preguntas[value].enunciado, TextView.BufferType.EDITABLE)
            var checked = -1
            preguntas[value].opciones.forEachIndexed { index, o ->
                val r = RadioButton(this)
                r.text = o.first
                if(o.second) checked = index
                radio_group.addView(r)
            }
            (radio_group.getChildAt(checked) as RadioButton).isChecked = true
            findViewById<Button>(R.id.crear_pregunta_seleccion).text = "Guardar"
            findViewById<Button>(R.id.crear_pregunta_seleccion).setOnClickListener {
                //Al pulsar el boton de crear pregunta se comprueba que se haya seleccionado
                //1 opcion y que el enunciado este relleno. En caso de error, se muestra
                //un alertDialog informando al usuario
                if (radio_group.checkedRadioButtonId == -1) {
                    builder.setMessage( getString(R.string.seleccionar_opcion)).setPositiveButton("OK", dialogClickListener).show()
                } else if (enunciado.text.isEmpty()) {
                    builder.setMessage(getString(R.string.enunciado_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
                } else {
                    //Si el usuario ha introducido la informacion necesaria y esta editando la pregunta,
                        // se añade a la posicion que tuviera en la lista de preguntas y se confirma al usuario
                            // que ha sido creada con exito
                    preguntas[value] = CrearTest.Pregunta(enunciado.text.toString(), "seleccion",
                        radio_group.children.filter { it is RadioButton }.map {
                            val r = it as RadioButton
                            Pair(r.text.toString(), r.isChecked)
                        }.toList()
                    )
                    Toast.makeText(this, getString(R.string.pregunta_guardada), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            findViewById<Button>(R.id.crear_pregunta_seleccion).setOnClickListener {
                //Al pulsar el boton de crear pregunta se comprueba que se haya seleccionado
                //1 opcion y que el enunciado este relleno. En caso de error, se muestra
                //un alertDialog informando al usuario
                if (radio_group.checkedRadioButtonId == -1) {
                    builder.setMessage( getString(R.string.seleccionar_opcion)).setPositiveButton("OK", dialogClickListener).show()
                } else if (enunciado.text.isEmpty()) {
                    builder.setMessage(getString(R.string.enunciado_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
                } else {
                    //Al estar creando una nueva pregunta, esta se añade a la lista de preguntas
                    // que se encuentra en el SingletonMap y se confirma al usuario que ha sido añadida
                    val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                    preguntas.add(CrearTest.Pregunta(enunciado.text.toString(), "seleccion",
                        radio_group.children.filter { it is RadioButton }.map {
                            val r = it as RadioButton
                            Pair(r.text.toString(), r.isChecked)
                        }.toList()
                    ))
                    Toast.makeText(this, getString(R.string.pregunta_guardada), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}