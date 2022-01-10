package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R

class SeleccionarTipoDePregunta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_tipo_de_pregunta)
        //Segun el boton seleccionado se abre una actividad u otra para crear un tipo de pregunta
        // determinado.
        findViewById<Button>(R.id.seleccion).setOnClickListener { view ->
            val intent = Intent(this, CrearPreguntaSeleccion::class.java)
            startActivity(intent)
            this.finish()
        }
        findViewById<Button>(R.id.multipleRespuesta).setOnClickListener { view ->
            val intent = Intent(this, CrearPreguntaMultipleRespuesta::class.java)
            startActivity(intent)
            this.finish()
        }
        findViewById<Button>(R.id.rellenar_huecos).setOnClickListener { view ->
            val intent = Intent(this, CrearPreguntaRellenarHuecos::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}