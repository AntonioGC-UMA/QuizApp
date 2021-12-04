package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


private lateinit var databaseReference: DatabaseReference
    var emailText : EditText? = null
    var passwordText : EditText? = null

    fun readData() : Pair<String, String>? {
        val email = emailText?.text.toString().trim()
        val password = passwordText?.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "El email no puede estár vacio", Toast.LENGTH_SHORT).show()
            return null
        }
        if (password.length < 6) {
            Toast.makeText(this, "La contraseña tiene que tener minimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return null
        }

        return Pair(email, password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        emailText = findViewById(R.id.email)
        passwordText = findViewById(R.id.password)

        val auth = FirebaseAuth.getInstance()
        val bd = Firebase.firestore

        SingletonMap["BD_AUTH"] = auth
        SingletonMap["BD"] = bd

        findViewById<Button>(R.id.login).setOnClickListener {
            readData()?.let { (email, password) ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Autenticacion correcta", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .addOnFailureListener{ exception ->
                        Toast.makeText(
                            this,
                            "Autenticacion fallida: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }

        findViewById<Button>(R.id.registrar).setOnClickListener {
            readData()?.let { (email, password) ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                        val usuario = hashMapOf(
                            "foto perfil" to "url de la fotaca",
                            "logros" to arrayListOf("tonto", "feo"),
                            "mis tests" to arrayListOf<DatabaseReference>(),
                            "tests realizados" to arrayListOf<DatabaseReference>()
                        )
                        bd.collection("usuarios")
                            .document(auth.currentUser?.uid.orEmpty())
                            .set(usuario)
                            .addOnFailureListener { Toast.makeText(this, "No se ha podido guardar información sobre el ususario: " + it.message, Toast.LENGTH_SHORT).show() }
                        val intent = Intent(this, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(baseContext, "Creación fallida: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}