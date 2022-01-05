package com.example.quizapp.activities

import android.R.attr
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.firestore.DocumentSnapshot
import android.R.attr.text

import android.R.attr.label

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.*


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

        findViewById<ImageView>(R.id.compartir).setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", test.id)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, getString(R.string.aviso_portapapeles), Toast.LENGTH_LONG).show()
        }

    }




    override fun onResume() {
        super.onResume()

        val resultado = SingletonMap["resultado test"]
        if (resultado != null) {
            SingletonMap.remove("resultado test")
            val (aciertos, fallos) = resultado as Pair<Int, Int>
            Toast.makeText(this, getString(R.string.has_acertado) + aciertos + getString(R.string.has_fallado) + fallos, Toast.LENGTH_SHORT).show()
        }
    }
}