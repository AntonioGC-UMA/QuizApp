package com.example.quizapp.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
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

        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                    }
                }
            }

        val builder = AlertDialog.Builder(this)

        val extras = getIntent().getExtras()
        val button_guardar_pregunta = Button(this)
        button_guardar_pregunta.text = getString(R.string.guardar_respuestas_rellenar_huecos)
        button_guardar_pregunta.setTextColor(ContextCompat.getColor(this,R.color.white))
        button_guardar_pregunta.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_500))

        if(extras != null){
            linear_layout_rellenar_huecos.removeAllViews()
            respuestas_huecos.clear()
            val value = extras.getInt("idx");
            val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
            texto.setText(preguntas[value].enunciado, TextView.BufferType.EDITABLE)
            layout_params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            preguntas[value].opciones.forEachIndexed { index, o ->
                val r = EditText(this)
                r.layoutParams = layout_params
                r.setText(o.first, TextView.BufferType.EDITABLE)
                linear_layout_rellenar_huecos.addView(r)
                respuestas_huecos.add(r)
            }


            button_guardar_pregunta.layoutParams = layout_params
            linear_layout_rellenar_huecos.addView(button_guardar_pregunta)
            if (!texto.text.isEmpty()){
                guardar_pregunta(button_guardar_pregunta, this, texto.text.toString(), value, builder, dialogClickListener)
            } else {
                builder.setMessage(getString(R.string.enunciado_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
                //Toast.makeText(this, getString(R.string.enunciado_no_vacio), Toast.LENGTH_SHORT).show()
            }

        }
        findViewById<Button>(R.id.btn_add_respuestas_rellenar_huecos).setOnClickListener { view ->
            if (texto.text.isEmpty()) {
                builder.setMessage(getString(R.string.enunciado_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
                //Toast.makeText(this, getString(R.string.enunciado_no_vacio), Toast.LENGTH_SHORT).show()
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
                    r.hint = getString(R.string.respuesta_rellenar_huecos) + " " + i
                    linear_layout_rellenar_huecos.addView(r)
                    respuestas_huecos.add(r)
                }
                button_guardar_pregunta.layoutParams = layout_params
                linear_layout_rellenar_huecos.addView(button_guardar_pregunta)

                guardar_pregunta(button_guardar_pregunta, this, t, -1, builder, dialogClickListener)

            }
        }
    }

    private fun guardar_pregunta(button:Button, context:Context, enunciado: String, index:Int, builder:AlertDialog.Builder,
    listener:DialogInterface.OnClickListener) {

        button.setOnClickListener{ view ->
            var rellenos = true
            for (edit in respuestas_huecos) {
                if(edit.text.isEmpty()){
                    rellenos = false
                }
            }
            if(!rellenos) {
                builder.setMessage(getString(R.string.aviso_rellenar_todos_los_huecos)).setPositiveButton("OK", listener).show()
                //Toast.makeText(context, getString(R.string.aviso_rellenar_todos_los_huecos), Toast.LENGTH_SHORT).show()
            } else {
                val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                if(index != -1){
                    preguntas[index] = CrearTest.Pregunta(enunciado, "rellenar huecos",
                        respuestas_huecos.map {
                            Pair(it.text.toString(), true)
                        }.toList()
                    )
                } else {
                    preguntas.add(CrearTest.Pregunta(enunciado, "rellenar huecos",
                        respuestas_huecos.map {
                            Pair(it.text.toString(), true)
                        }.toList()
                    ))
                }
                Toast.makeText(context, getString(R.string.pregunta_guardada), Toast.LENGTH_SHORT).show()
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