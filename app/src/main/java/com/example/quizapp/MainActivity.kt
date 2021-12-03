package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.ref.PhantomReference

class MainActivity : AppCompatActivity() {


private lateinit var databaseReference: DatabaseReference
    var emailText : EditText? = null
    var passwordText : EditText? = null

    fun readData() : Pair<String, String>? {
        val email = emailText?.text.toString().trim()
        val password = passwordText?.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "El email no puede estár vacio", Toast.LENGTH_SHORT).show()
            return null;
        }
        if (password.length < 6) {
            Toast.makeText(this, "La contraseña tiene que tener minimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return null;
        }

        return Pair(email, password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        emailText = findViewById<EditText>(R.id.email)
        passwordText = findViewById<EditText>(R.id.password)

        val auth = FirebaseAuth.getInstance()
        val bd = Firebase.firestore

        SingletonMap.put("BD_AUTH", auth)
        SingletonMap.put("BD", bd)

        findViewById<Button>(R.id.login).setOnClickListener {
            val data = readData()
            data?.let {
                val (email, password) = data
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Autenticacion correcta", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, Home::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Autenticacion fallida: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        findViewById<Button>(R.id.registrar).setOnClickListener {


            val data = readData()

            data?.let {
                val (email, password) = data

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                                //TO DO: Crear la entidad usuario y guardar la info
                                //databaseReference.child(auth.currentUser?.uid.toString()).setValue()
                                val intent = Intent(this, Home::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            } else {
                                Toast.makeText(baseContext, "Creación fallida: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }
    }
}