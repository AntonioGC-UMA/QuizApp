package com.example.quizapp.activities

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap


class CrearPreguntaRellenarHuecos : AppCompatActivity() {
    lateinit var linear_layout_rellenar_huecos : LinearLayout
    lateinit var layout_params : LinearLayout.LayoutParams
    lateinit var respuestas_huecos : MutableList<EditText>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_pregunta_rellenar_huecos)

        val texto = findViewById<EditText>(R.id.respuesta_enunciado)
        linear_layout_rellenar_huecos = findViewById(R.id.respuestas_rellenar_huecos)
        respuestas_huecos = mutableListOf()

        findViewById<Button>(R.id.btn_add_respuestas_rellenar_huecos).setOnClickListener { view ->
            if (texto.text.isEmpty()) {
                Toast.makeText(this, "El enunciado no puede estar vacío", Toast.LENGTH_SHORT)
            } else {
                linear_layout_rellenar_huecos.removeAllViews()
                respuestas_huecos.clear()
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

                crear_boton_y_guardar_pregunta(this, t)

            }
        }
    }

    private fun crear_boton_y_guardar_pregunta(context: Context, enunciado: String) {

        val button_guardar_pregunta = Button(context)
        button_guardar_pregunta.layoutParams = layout_params
        button_guardar_pregunta.text = getString(R.string.guardar_respuestas_rellenar_huecos)
        button_guardar_pregunta.setTextColor(ContextCompat.getColor(this,R.color.white))
        button_guardar_pregunta.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_500))
        linear_layout_rellenar_huecos.addView(button_guardar_pregunta)
        button_guardar_pregunta.setOnClickListener{ view ->
            var rellenos = true
            for (edit in respuestas_huecos) {
                if(edit.text.isEmpty()){
                    rellenos = false
                }
            }
            println(rellenos)
            if(!rellenos) {
                Toast.makeText(context, "Algunos huecos no están rellenos", Toast.LENGTH_SHORT).show()
            } else {
                val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                preguntas.add(CrearTest.Pregunta(enunciado, "rellenar huecos",
                    respuestas_huecos.map {
                        Pair(it.text.toString(), true)
                    }.toList()
                ))
                finish()
            }
        }

    }

    fun countMatches(string: String, pattern: String): Int {
        return string.split(pattern)
            .dropLastWhile { it.isEmpty() }
            .toTypedArray().size - 1
    }
}