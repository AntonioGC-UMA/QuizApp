package com.example.quizapp.activities

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


        val texto = findViewById<EditText>(R.id.texto_seleccion)
        val radio_group = findViewById<RadioGroup>(R.id.radio_group)
        val enunciado = findViewById<EditText>(R.id.enunciado_seleccion)

        findViewById<Button>(R.id.nueva_seleccion).setOnClickListener {
            if (texto.text.isEmpty()) {
                Toast.makeText(this, getString(R.string.texto_no_vacio), Toast.LENGTH_SHORT).show()
            } else {

                val t = texto.text.toString()
                texto.text.clear()
                val r = RadioButton(this)
                r.text = t
                radio_group.addView(r)
            }
        }
        findViewById<Button>(R.id.cancelar_crear_pregunta_conectar).setOnClickListener {
            // TODO: poner un: estas seguro?
            finish()
        }

        val extras = intent.extras

        if (extras != null) {
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
                if (radio_group.checkedRadioButtonId == -1) {
                    Toast.makeText(this, getString(R.string.seleccionar_opcion), Toast.LENGTH_SHORT).show()
                } else if (enunciado.text.isEmpty()) {
                    Toast.makeText(this, getString(R.string.enunciado_no_vacio), Toast.LENGTH_SHORT).show()
                } else {
                    preguntas[value] = CrearTest.Pregunta(enunciado.text.toString(), "seleccion",
                        radio_group.children.filter { it is RadioButton }.map {
                            val r = it as RadioButton
                            Pair(r.text.toString(), r.isChecked)
                        }.toList()
                    )
                    finish()
                }
            }
        } else {
            findViewById<Button>(R.id.crear_pregunta_seleccion).setOnClickListener {
                if (radio_group.checkedRadioButtonId == -1) {
                    Toast.makeText(this, getString(R.string.seleccionar_opcion), Toast.LENGTH_SHORT).show()
                } else if (enunciado.text.isEmpty()) {
                    Toast.makeText(this, getString(R.string.enunciado_no_vacio), Toast.LENGTH_SHORT).show()
                } else {
                    val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                    preguntas.add(CrearTest.Pregunta(enunciado.text.toString(), "seleccion",
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
}