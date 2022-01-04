package com.example.quizapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import android.widget.CheckBox




class CrearPreguntaMultipleRespuesta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_pregunta_multiple)
        val nuevaOpcion = findViewById<EditText>(R.id.nuevaOpcion)
        val checkboxes = findViewById<LinearLayout>(R.id.checkboxes)
        val enunciado = findViewById<EditText>(R.id.enunciado_multiple)

        findViewById<Button>(R.id.button_anadir).setOnClickListener {
            if (nuevaOpcion.text.isEmpty()) {
                Toast.makeText(this, getString(R.string.texto_no_vacio), Toast.LENGTH_SHORT).show()
            } else {

                val t = nuevaOpcion.text.toString()
                nuevaOpcion.text.clear()
                val cb = CheckBox(this)
                cb.text = t
                checkboxes.addView(cb)
            }
        }


        findViewById<Button>(R.id.cancelar_crear_pregunta_multiple).setOnClickListener { view ->
            // TODO: poner un: estas seguro?
            finish()
        }


        fun CheckedCount(group: LinearLayout): Int {
            var n = 0
            for ( checkbox in group.children) {
                var c = checkbox as CheckBox
                if(c.isChecked == true){
                    n++
                }
            }
            return n
        }

        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                    }
                }
            }

        val builder = AlertDialog.Builder(this)

        val extras = getIntent().getExtras();

        if (extras != null) {
            val value = extras.getInt("idx");
            val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
            enunciado.setText(preguntas[value].enunciado, TextView.BufferType.EDITABLE)
            preguntas[value].opciones.forEachIndexed { index, o ->
                val c = CheckBox(this)
                c.text = o.first
                c.isChecked = o.second
                checkboxes.addView(c)
            }
            findViewById<Button>(R.id.button_crear).text = "Guardar"
            findViewById<Button>(R.id.button_crear).setOnClickListener {
                if (CheckedCount(checkboxes) <= 0) {
                    builder.setMessage(getString(R.string.seleccionar_opcion)).setPositiveButton("OK", dialogClickListener).show()
                    //Toast.makeText(this, getString(R.string.seleccionar_opcion), Toast.LENGTH_SHORT).show()
                } else if (enunciado.text.isEmpty()) {
                    builder.setMessage(getString(R.string.enunciado_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
                    //Toast.makeText(this, getString(R.string.enunciado_no_vacio), Toast.LENGTH_SHORT).show()
                } else {
                    val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                    // TODO: poner un enunciado
                    preguntas[value] = (CrearTest.Pregunta(enunciado.text.toString(), "multiple",
                        checkboxes.children.filter { it is CheckBox }.map {
                            val c = it as CheckBox
                            Pair(c.text.toString(), c.isChecked)
                        }.toList()
                    ))
                    finish()
                }
            }

        }else{

            findViewById<Button>(R.id.button_crear).setOnClickListener { view ->

                if (checkboxes.childCount == 0) {
                    builder.setMessage(getString(R.string.aviso_crear_alguna_opcion)).setPositiveButton("OK", dialogClickListener).show()
                    //Toast.makeText(this,getString(R.string.aviso_crear_alguna_opcion) , Toast.LENGTH_SHORT).show()
                } else if(CheckedCount(checkboxes) == 0){
                    builder.setMessage(getString(R.string.aviso_seleccionar_alguna_opcion)).setPositiveButton("OK", dialogClickListener).show()
                    //Toast.makeText(this, getString(R.string.aviso_seleccionar_alguna_opcion), Toast.LENGTH_SHORT).show()
                }else if(enunciado.text.isEmpty()){
                    builder.setMessage(getString(R.string.aviso_enunciado_correcto)).setPositiveButton("OK", dialogClickListener).show()
                    //Toast.makeText(this, getString(R.string.aviso_enunciado_correcto), Toast.LENGTH_SHORT).show()
                }else {
                    val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
                    // TODO: poner un enunciado
                    preguntas.add(CrearTest.Pregunta(enunciado.text.toString(), "multiple",
                        checkboxes.children.filter { it is CheckBox }.map {
                            val c = it as CheckBox
                            Pair(c.text.toString(), c.isChecked)
                        }.toList()
                    ))
                    finish()
                }
            }

        }
    }
}

