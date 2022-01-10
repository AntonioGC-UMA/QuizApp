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

        //Al pulsar el boton añadir opcion, si no se especifica un texto para dicha opcion, se muestra
        //un mensaje de error si no, se añade la opcion a la lista de opciones
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

        //Si el usuario cancela la creacion de la pregunta, se cierra la actividad actual
        findViewById<Button>(R.id.cancelar_crear_pregunta_multiple).setOnClickListener { view ->
            finish()
        }

        //Devuelve el numero de checkboxes marcados como verdaderos
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

        //Codigo necesario para poder mostrar un AlertDialog
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                    }
                }
            }

        val builder = AlertDialog.Builder(this)

        val extras = getIntent().getExtras();
        //La variable extras contiene informacion necesaria para saber si se esta creando una nueva
        //pregunta o se esta editando una pregunta que ha sido previamente creada
        if (extras != null) {
            //Si extras no es nulo, estamos editando la pregunta por lo que hay que mostrar la informacion
                // relevante de la misma como las opciones marcadas por el usuario como verdaderas anteriormente
                    // o el enunciado
            val value = extras.getInt("idx");
            val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
            enunciado.setText(preguntas[value].enunciado, TextView.BufferType.EDITABLE)
            preguntas[value].opciones.forEachIndexed { index, o ->
                val c = CheckBox(this)
                c.text = o.first
                c.isChecked = o.second
                checkboxes.addView(c)
            }
            findViewById<Button>(R.id.button_crear).text = getString(R.string.guardar)
            findViewById<Button>(R.id.button_crear).setOnClickListener {
                if (CheckedCount(checkboxes) <= 0) {
                    builder.setMessage(getString(R.string.seleccionar_opcion)).setPositiveButton("OK", dialogClickListener).show()
                } else if (enunciado.text.isEmpty()) {
                    builder.setMessage(getString(R.string.enunciado_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
                } else {
                    val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
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
            //En otro caso, estamos creando una nueva pregunta por lo que hay que comprobar que el usuario
            // introduce la informacion necesaria para crear una pregunta como las opciones marcadas
                // y cuales son verdaderas o el enunciado. En caso de no introducir dicha informacion
                    //se muestra un AlertDialog
            findViewById<Button>(R.id.button_crear).setOnClickListener { view ->

                if (checkboxes.childCount == 0) {
                    builder.setMessage(getString(R.string.aviso_crear_alguna_opcion)).setPositiveButton("OK", dialogClickListener).show()
                } else if(CheckedCount(checkboxes) == 0){
                    builder.setMessage(getString(R.string.aviso_seleccionar_alguna_opcion)).setPositiveButton("OK", dialogClickListener).show()
                }else if(enunciado.text.isEmpty()){
                    builder.setMessage(getString(R.string.aviso_enunciado_correcto)).setPositiveButton("OK", dialogClickListener).show()
                }else {
                    val preguntas = SingletonMap["lista_preguntas"] as MutableList<CrearTest.Pregunta>
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

