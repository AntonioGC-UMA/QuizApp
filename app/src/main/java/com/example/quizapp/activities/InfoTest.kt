package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InfoTest : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {
    val test = SingletonMap["lastTest"] as DocumentSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_test)


        findViewById<TextView>(R.id.label_titulo).text = test.get("titulo") as String
        findViewById<TextView>(R.id.label_descripcion).text = test.get("descripcion") as String
        findViewById<TextView>(R.id.label_categorias).text = (test.get("categorias") as List<String>).joinToString(" ")
        findViewById<TextView>(R.id.lbl_preguntas).text = (test.get("preguntas") as List<HashMap<String,*>>).size.toString()
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

    override fun onResume() {
        super.onResume()
        val rBar = findViewById<RatingBar>(R.id.calificacion_test)
        rBar.onRatingBarChangeListener = this
        val resultado = SingletonMap["resultado test"]
        if (resultado != null) {
            SingletonMap.remove("resultado test")
            val (aciertos, fallos) = resultado as Pair<Int, Int>
            Toast.makeText(this, "Has acertado " + aciertos + " y has fallado " + fallos, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
        Firebase.firestore.collection("tests")
            .document(test.id).update("valoracion", p1).addOnSuccessListener {
                Toast.makeText(this, "Test valorado con exito", Toast.LENGTH_SHORT).show()
            }
    }
}
