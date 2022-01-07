package com.example.quizapp.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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

        findViewById<ImageView>(R.id.compartir).setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", test.id)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, getString(R.string.aviso_portapapeles), Toast.LENGTH_LONG).show()
        }

    }

    override fun onResume() {
        super.onResume()
        val rBar = findViewById<RatingBar>(R.id.calificacion_test)
        rBar.onRatingBarChangeListener = this

        val calificacion = findViewById<TextView>(R.id.calificacion)
        val resultado = SingletonMap["resultado test"]
        if (resultado != null) {
            SingletonMap.remove("resultado test")
            val (aciertos, fallos) = resultado as Pair<Int, Int>
            calificacion.text = getString(R.string.has_acertado) + " " +  aciertos + " " + getString(R.string.has_fallado) + " " + fallos
            Firebase.firestore.collection("usuarios").document(FirebaseAuth.getInstance().uid!!).update("tests realizados", FieldValue.arrayUnion(test.reference))
        }
    }

    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
        Firebase.firestore.collection("tests")
            .document(test.id).update("valoracion", p1).addOnSuccessListener {
                Toast.makeText(this, getString(R.string.test_valorado_exito), Toast.LENGTH_SHORT).show()
            }
    }
}
