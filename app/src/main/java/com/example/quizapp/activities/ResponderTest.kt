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
            "multiple" -> {
                for(o in p.opciones){
                    val checkBox = CheckBox(this)
                    checkBox.text = o.first
                    opciones.addView(checkBox)
                }
            }
            "rellenar huecos" -> {
                for(o in p.opciones){
                    val editText = EditText(this)
                    opciones.addView(editText)
                }
            }
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
                    solucion.text = getString(R.string.correcto)
                    aciertos_value += 1
                } else {
                    fallos_value += 1
                    solucion.text = errores.joinToString("\n") {
                        (it.value as RadioButton).text.toString() + " " +getString(R.string.deberia_ser) + " " + p.opciones[it.index].second.toString()
                    }
                }
            }
            "multiple" -> {
                val errores = opciones.children.filter { it is CheckBox }
                    .withIndex()
                    .filter {
                        val r = it.value as CheckBox
                        p.opciones[it.index].second != r.isChecked
                    }.toList()
                if (errores.isEmpty()) {
                    solucion.text = getString(R.string.correcto)
                    aciertos_value += 1
                } else {
                    fallos_value += 1
                    solucion.text = errores.joinToString("\n") {
                        (it.value as CheckBox).text.toString() + getString(R.string.deberia_ser) + p.opciones[it.index].second.toString()
                    }
                }

            }
            "rellenar huecos" -> {
                 val errores = opciones.children.filter { it is EditText }
                    .withIndex()
                    .filter {
                        val r = it.value as EditText
                        p.opciones[it.index].first != r.text.toString()
                    }.toList()
                if (errores.isEmpty()) {
                    solucion.text = getString(R.string.correcto)
                    aciertos_value += 1
                } else {
                    fallos_value += 1
                    solucion.text = errores.joinToString("\n") {
                        (it.value as EditText).text.toString() + " " + getString(R.string.deberia_ser) + " " + p.opciones[it.index].first.toString()
                    }
                }
            }
        }
        siguiente.text = getString(R.string.comprobar_pregunta)
        actualizar_aciertos()
        val es_ultimo = (idx + 1) >= preguntas.size
        if (es_ultimo) {
            siguiente.text = getString(R.string.finalizar_test)
        } else {
            siguiente.text = getString(R.string.siguiente_pregunta)
        }
        siguiente.setOnClickListener {
            idx += 1
            if(es_ultimo) {
                SingletonMap["resultado test"] = Pair(aciertos_value, fallos_value)
                this.finish()
            } else {
                cargar_pregunta(preguntas[idx])
            }
        }
    }

    fun actualizar_aciertos() {
        aciertos.text = aciertos_value.toString() + " " + getString(R.string.preguntas)
        fallos.text = fallos_value.toString() + " " + getString(R.string.preguntas)
        progreso.text = getString(R.string.progreso) + " " + (aciertos_value + fallos_value).toString() + "/" + preguntas.size.toString() + " " + getString(R.string.preguntas)
    }
}