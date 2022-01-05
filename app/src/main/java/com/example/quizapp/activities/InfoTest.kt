package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.firestore.DocumentSnapshot

class InfoTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_test)

        val test = SingletonMap["lastTest"] as DocumentSnapshot

        findViewById<TextView>(R.id.label_titulo).text = test.get("titulo") as String
        findViewById<TextView>(R.id.label_descripcion).text = test.get("descripcion") as String
        findViewById<TextView>(R.id.label_categorias).text = (test.get("categorias") as List<String>).joinToString(" ")

        findViewById<Button>(R.id.realizar_test).setOnClickListener {
            SingletonMap["lista_preguntas"] = (test["preguntas"] as List<HashMap<String,*>>).map { pregunta ->
                CrearTest.Pregunta(
                    pregunta["enunciado"] as String,
                    pregunta["tipo"] as String,
                    (pregunta["opciones"] as List<HashMap<String, *>>).map { opcion ->
                        Pair(opcion["respuesta"] as String, opcion["correcta"] as Boolean)
                    })
            }


            val intent = Intent(this, ResponderTest::class.java)
            startActivity(intent)
        }
    }




}