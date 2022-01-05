package com.example.quizapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.firestore.DocumentSnapshot

class InfoTest : AppCompatActivity() {
    private var preguntas = mutableListOf<CrearTest.Pregunta>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_test)

        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                    }
                }
            }

        fun List<String>.concat() = this.joinToString(",") { it }.takeWhile { it.isDefined() }

        val Test = SingletonMap["lastTest"] as DocumentSnapshot

        findViewById<TextView>(R.id.label_titulo).text = Test.get("titulo") as String
        findViewById<TextView>(R.id.label_descripcion).text = Test.get("descripcion") as String
        findViewById<TextView>(R.id.label_categorias).text = (Test.get("categorias") as List<String>)[0]

/*
        preguntas = (Test["preguntas"] as List<HashMap<String,*>>).map { pregunta ->
            CrearTest.Pregunta(
                pregunta["enunciado"] as String,
                pregunta["tipo"] as String,
                (pregunta["opciones"] as List<HashMap<String, *>>).map { opcion ->
                    Pair(opcion["respuesta"] as String, opcion["correcta"] as Boolean)
                })
        } as MutableList<CrearTest.Pregunta>
        SingletonMap["lista_preguntas"] = preguntas
        CrearTest.actualizar_recicler_view()
*/

    }




}