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
        //Localizacion de los campos de texto y elementos graficos cuyo texto habra que modificar
        aciertos = findViewById(R.id.texto_aciertos)
        fallos = findViewById(R.id.texto_fallos)
        progreso = findViewById(R.id.texto_progreso)
        enunciado = findViewById(R.id.texto_enunciado)
        solucion = findViewById(R.id.texto_solucion)
        siguiente = findViewById(R.id.boton_siguiente)
        opciones = findViewById(R.id.lista_opciones)

        //Se obtiene la lista de preguntas
        preguntas = SingletonMap["lista_preguntas"] as List<CrearTest.Pregunta>

        //Se actualizan los aciertos(inicialmente 0 al cargar la actividad) y se carga la primera
        //pregunta del test
        actualizar_aciertos()
        cargar_pregunta(preguntas[idx])
    }

    fun cargar_pregunta(p: CrearTest.Pregunta) {
        //Se eliminan las opciones que hubieran de preguntas anteriores, se elimina la solucion y
        //se muestra el enunciado
        opciones.removeAllViews()
        solucion.text = ""
        enunciado.text = p.enunciado

        //Dependiendo del tipo de pregunta, se deberan crear los elementos graficos necesarios para
        //mostrar las opciones y huecos a rellenar. Si es una pregunta de seleccion, se crean los
        //radio buttons necesarios, si es una pregunta de respuesta multiple, se crean tantos checkboxes
        //como opciones tenga la pregunta y si es una pregunta de rellenar huecos, se crean los EditText
        //necesarios para responder
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
        //Se modifica el texto del boton siguiente para comprobar la respuesta y al pulsar dicho
        //boton se corrige la pregunta
        siguiente.text = getString(R.string.comprobar_pregunta)
        siguiente.setOnClickListener {
            verificar_pregunta(preguntas[idx])
        }
    }

    fun verificar_pregunta(p: CrearTest.Pregunta) {
        //Nuevamente, dependiendo del tipo de pregunta, se comprueba que el usuario ha marcado los
        //campos correctos.
        // En caso de preguntas de seleccion, se comprueba que las opciones marcadas
        //sean las mismas que estaban predefinidas en la pregunta. En caso de exito, se suma un acierto
        //y se muestra un mensaje de confirmacion y en caso de fallo, ademas de sumar un error mas, se
        //muestra un mensaje con la/s opcion/es correcta/s.
        //Para las preguntas de multiple respuesta, se comprueba que los checkboxes marcados por el usuario
        //coinciden con los asignados como ciertos. Si coincide, se muestra un mensaje de confirmacion y se aumentan
        //los aciertos y en caso de error, se aumenta en uno el numero de fallos y se muestra la respuesta correcta.
        //Para las preguntas de rellenar huecos, se comprueba que campos son diferentes a las respuestas que deberian
        //ser correctas. Si todos los campos coinciden, se incrementa el numero de aciertos y se muestra un mensaje
        //de confirmacion y si la respuesta introducida era erronea, se muestra un mensaje con la respuesta correcta y se
        //incrementa en uno el numero de fallos.
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
                        (it.value as CheckBox).text.toString() + " " + getString(R.string.deberia_ser) + " " + p.opciones[it.index].second.toString()
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

        //Tras responder una pregunta, se actualiza el progreso del usuario en el test
        actualizar_aciertos()
        //Si es la ultima pregunta por responder, habra que finalizar el test, en cambio, si aun hay
        //mas preguntas, se debera pasar a la siguiente
        val es_ultimo = (idx + 1) >= preguntas.size
        if (es_ultimo) {
            siguiente.text = getString(R.string.finalizar_test)
        } else {
            siguiente.text = getString(R.string.siguiente_pregunta)
        }
        //Cuando se pulsa el boton de siguiente o finalizar, si es la ultima pregunta, se calculan los
        //aciertos y fallos cometidos y si aun hay preguntas por responder se carga la pregunta siguiente
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
    //Actualizacion del progreso del usuario en el test asi como del numero de aciertos y fallos tras
    //responder una pregunta
    fun actualizar_aciertos() {
        aciertos.text = aciertos_value.toString() + " " + getString(R.string.preguntas_acertadas)
        fallos.text = fallos_value.toString() + " " + getString(R.string.preguntas_falladas)
        progreso.text = getString(R.string.progreso) + " " + (aciertos_value + fallos_value).toString() + "/" + preguntas.size.toString() + " " + getString(R.string.preguntas)
    }
}