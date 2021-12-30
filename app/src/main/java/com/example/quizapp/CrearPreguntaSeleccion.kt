package com.example.quizapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.quizapp.activities.CrearTest
import com.example.quizapp.entities.SingletonMap

class CrearPreguntaSeleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_pregunta_seleccion)
        val texto = findViewById<EditText>(R.id.texto_seleccion)
        val radio_group = findViewById<RadioGroup>(R.id.radio_group)
        findViewById<Button>(R.id.nueva_seleccion).setOnClickListener { view ->
            if (texto.text.isEmpty()) {
                Toast.makeText(this, "El texto no puede estar vacio", Toast.LENGTH_SHORT)
            } else {

                val t = texto.text.toString()
                texto.text.clear()
                val r = RadioButton(this)
                r.text = t;
                radio_group.addView(r)
            }
        }

        findViewById<Button>(R.id.cancelar_crear_pregunta_conectar).setOnClickListener { view ->
            // TODO: poner un: estas seguro?
            finish()
        }
        findViewById<Button>(R.id.crear_pregunta_conectar).setOnClickListener { view ->
            if (radio_group.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Tienes que seleccionar una opcion", Toast.LENGTH_SHORT)
            } else {
                var preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                // TODO: poner un enunciado
                preguntas.add(CrearTest.Pregunta("TODO, poner un enunciado", "seleccion",
                    radio_group.children.filter { it is RadioButton }.map {
                        val r = it as RadioButton
                        Pair(r.text.toString(), r.isChecked)
                    }.toList()
                ))
                finish()
            }
        }
    }
}