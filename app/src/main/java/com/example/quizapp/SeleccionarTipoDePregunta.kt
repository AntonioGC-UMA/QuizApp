package com.example.quizapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SeleccionarTipoDePregunta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_tipo_de_pregunta)

        findViewById<Button>(R.id.seleccion).setOnClickListener { view ->
            println("Seleccion")
            /*
            val intent = Intent(this, SeleccionarTipoDePregunta::class.java)
            startActivity(intent)
             */
            this.finish()
        }
        findViewById<Button>(R.id.multipleRespuesta).setOnClickListener { view ->
            println("Multiple respuesta")
            /*
            val intent = Intent(this, SeleccionarTipoDePregunta::class.java)
            startActivity(intent)
             */
            this.finish()
        }
        findViewById<Button>(R.id.conectar).setOnClickListener { view ->
            println("Conectar")
            /*
            val intent = Intent(this, SeleccionarTipoDePregunta::class.java)
            startActivity(intent)
             */
            this.finish()
        }
    }
}