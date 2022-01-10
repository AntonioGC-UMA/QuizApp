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
    val valoracion_any = test.get("valoracion")

    var valoracion = if (valoracion_any is Long) {(valoracion_any).toDouble()} else {valoracion_any as Double}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_test)
        //Al mostrar la informacion del test es necesario mostrar campos como el titulo, descripcion, categorias y
        //numero de preguntas que es lo que se hace en las siguientes lineas de codigo.
        findViewById<TextView>(R.id.label_titulo).text = test.get("titulo") as String
        findViewById<TextView>(R.id.label_descripcion).text = test.get("descripcion") as String
        findViewById<TextView>(R.id.label_categorias).text = (test.get("categorias") as List<String>).joinToString(" ")
        findViewById<TextView>(R.id.lbl_preguntas).text = (test.get("preguntas") as List<HashMap<String,*>>).size.toString()
        //Al pulsar sobre realizar test, se añaden las preguntas del test a la lista de preguntas del singletonMap
        //que seran recuperadas al mostrar la actividad responder test y se muestra dicha actividad para comenzar
        // a contestar el test.
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

        //Al pulsar sobre el boton compartir, se copia al portapapeles del dispositivo el codigo del test
        //y se muestra un mensaje de confirmacion
        findViewById<ImageView>(R.id.compartir).setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", test.id)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, getString(R.string.aviso_portapapeles), Toast.LENGTH_LONG).show()
        }

    }

    override fun onResume() {
        super.onResume()
        //Se ofrece la posibilidad de valorar un test, para ello se muestra la valoracion que tuviera anteriormente
        //dicho test y se asigna un controlador para detectar cuando el usuario cambia la valoracion del test
        val rBar = findViewById<RatingBar>(R.id.calificacion_test)
        rBar.rating = valoracion.toFloat()
        rBar.onRatingBarChangeListener = this

        //Tras realizar el test se obtiene el resultado del mismo, mostrando un mensaje al usuario
        //con al cantidad de aciertos y fallos que ha cometido y se añade dicho test a la lista de
        //tests realizados por el usuario
        val calificacion = findViewById<TextView>(R.id.calificacion)
        val resultado = SingletonMap["resultado test"]
        if (resultado != null) {
            SingletonMap.remove("resultado test")
            val (aciertos, fallos) = resultado as Pair<Int, Int>
            calificacion.text = getString(R.string.has_acertado) + " " +  aciertos + getString(R.string.preguntas) + ", " + getString(R.string.has_fallado) + " " + fallos + " " + getString(R.string.preguntas)
            Firebase.firestore.collection("usuarios").document(FirebaseAuth.getInstance().uid!!).update("tests realizados", FieldValue.arrayUnion(test.reference))
        }
    }

    //Cuando se detecta un cambio en la valoracion de un test, se calcula la media entre la nueva valoracion
    //y la que ya tuviera en la BD anteriormente y se guarda actualiza la valoracion del test. Ademas,
    //se muestra un mensaje de confirmacion
    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
        val media = (p1 + valoracion)/2
        Firebase.firestore.collection("tests")
            .document(test.id).update("valoracion", media).addOnSuccessListener {
                Toast.makeText(this, getString(R.string.test_valorado_exito), Toast.LENGTH_SHORT).show()
            }
    }
}
