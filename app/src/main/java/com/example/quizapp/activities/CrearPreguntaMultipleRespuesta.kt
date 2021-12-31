package com.example.quizapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.quizapp.R
import android.widget.TextView
import com.google.api.Distribution


class CrearPreguntaMultipleRespuesta : AppCompatActivity() {
    lateinit var linear_layout_rellenar_huecos : LinearLayout
    lateinit var layout_params : LinearLayout.LayoutParams
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_pregunta_rellenar_huecos)

        val texto = findViewById<EditText>(R.id.respuesta_enunciado)
        linear_layout_rellenar_huecos = findViewById<LinearLayout>(R.id.respuestas_rellenar_huecos)
        val respuestas_huecos = mutableListOf<EditText>()

        findViewById<Button>(R.id.btn_add_respuestas_rellenar_huecos).setOnClickListener { view ->
            if (texto.text.isEmpty()) {
                Toast.makeText(this, "El enunciado no puede estar vacío", Toast.LENGTH_SHORT)
            } else {
                val t = texto.text.toString()
                val pattern = "..."
                val matches = countMatches(t, pattern)
                println(matches)
                layout_params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                for (i in 1..matches) {
                    val r = EditText(this)
                    r.layoutParams = layout_params
                    r.hint = "Escribe la respuesta " + i
                    linear_layout_rellenar_huecos.addView(r)
                    respuestas_huecos.add(r)
                }

                crear_boton_y_guardar_pregunta(this)

            }
        }
    }

    private fun crear_boton_y_guardar_pregunta(context: Context) {
        val button_guardar_pregunta = Button(context)
        button_guardar_pregunta.layoutParams = layout_params
        linear_layout_rellenar_huecos.addView(button_guardar_pregunta)
    }

    fun countMatches(string: String, pattern: String): Int {
        return string.split(pattern)
            .dropLastWhile { it.isEmpty() }
            .toTypedArray().size - 1
    }
}