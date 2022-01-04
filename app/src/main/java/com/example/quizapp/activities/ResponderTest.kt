package com.example.quizapp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap

class ResponderTest : AppCompatActivity() {

    lateinit var aciertos: TextView
    lateinit var fallos: TextView
    lateinit var progreso: TextView
    lateinit var enunciado: TextView
    lateinit var solucion: TextView
    lateinit var siguiente: Button
    lateinit var opciones: LinearLayout

    lateinit var preguntas: List<CrearTest.Pregunta>
    var idx = 0

    var aciertos_value = 0
    var fallos_value = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_test)

        aciertos = findViewById(R.id.texto_aciertos)
        fallos = findViewById(R.id.texto_fallos)
        progreso = findViewById(R.id.texto_progreso)
        enunciado = findViewById(R.id.texto_enunciado)
        solucion = findViewById(R.id.texto_solucion)
        siguiente = findViewById(R.id.boton_siguiente)
        opciones = findViewById(R.id.lista_opciones)


        // TODO esto hay que pasarselo o que nos pasen el id del test y lo sacamos de la base de datos
        preguntas = SingletonMap["lista_preguntas"] as List<CrearTest.Pregunta>

        actualizar_aciertos()
        cargar_pregunta(preguntas[idx])
    }

    fun cargar_pregunta(p: CrearTest.Pregunta) {
        opciones.removeAllViews()
        solucion.text = ""
        enunciado.text = p.enunciado

        when (p.tipo) {
            "seleccion" -> {
                val rg = RadioGroup(this)
                rg.id = R.id.radio_group
                opciones.addView(rg)
                for (o in p.opciones) {
                    val rb = RadioButton(this)
                    rb.text = o.first
                    rg.addView(rb)
                }
            }
            "multiple" -> {}
            "rellenar huecos" -> {}
        }

        siguiente.setOnClickListener {
            verificar_pregunta(preguntas[idx])
        }
    }

    fun verificar_pregunta(p: CrearTest.Pregunta) {
        when (p.tipo) {
            "seleccion" -> {
                val errores = opciones.findViewById<RadioGroup>(R.id.radio_group).children
                    .filter { it is RadioButton }
                    .withIndex()
                    .filter {
                        val r = it.value as RadioButton
                        p.opciones[it.index].second != r.isChecked
                    }.toList()

                if (errores.isEmpty()) {
                    solucion.text = "Correcto!"
                    aciertos_value += 1
                } else {
                    fallos_value += 1
                    solucion.text = errores.joinToString("\n") {
                        (it.value as RadioButton).text.toString() + " debería ser " + p.opciones[it.index].second.toString()
                    }
                }
            }
            "multiple" -> {}
            "rellenar huecos" -> {}
        }

        actualizar_aciertos()

        siguiente.setOnClickListener {
            idx += 1
            cargar_pregunta(preguntas[idx])
        }
    }

    fun actualizar_aciertos() {
        aciertos.text = aciertos_value.toString()
        fallos.text = fallos_value.toString()
        progreso.text = (aciertos_value + fallos_value).toString() + "/" + preguntas.size.toString()
    }
}