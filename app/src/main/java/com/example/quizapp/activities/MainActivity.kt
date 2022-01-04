package com.example.quizapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


private lateinit var databaseReference: DatabaseReference
    var emailText : EditText? = null
    var passwordText : EditText? = null
    val dialogClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                }
            }
        }

    fun readData() : Pair<String, String>? {
        val email = emailText?.text.toString().trim()
        val password = passwordText?.text.toString().trim()
        val builder = AlertDialog.Builder(applicationContext)

        if (email.isEmpty()) {
            builder.setMessage(getString(R.string.email_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
            //Toast.makeText(this, getString(R.string.email_no_vacio), Toast.LENGTH_SHORT).show()
            return null
        }
        if (password.length < 6) {
            builder.setMessage(getString(R.string.pass_min_6)).setPositiveButton("OK", dialogClickListener).show()
            /*Toast.makeText(
                this,
                getString(R.string.pass_min_6),
                Toast.LENGTH_SHORT
            ).show()*/
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
        val builder = AlertDialog.Builder(applicationContext)

        SingletonMap["BD_AUTH"] = auth
        SingletonMap["BD"] = bd

        findViewById<Button>(R.id.login).setOnClickListener {
            readData()?.let { (email, password) ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.auth_correct), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .addOnFailureListener{ exception ->
                        builder.setMessage(getString(R.string.auth_fail) + " ${exception.message}").setPositiveButton("OK", dialogClickListener).show()
                        /* Toast.makeText(
                             this,
                             getString(R.string.auth_fail) + " ${exception.message}",
                             Toast.LENGTH_LONG
                         ).show()*/
                    }
                }

        }

        findViewById<Button>(R.id.registrar).setOnClickListener {
            readData()?.let { (email, password) ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.usuario_creado_exito), Toast.LENGTH_SHORT).show()
                        val usuario = hashMapOf(
                            "foto perfil" to "url de la fotaca",
                            "logros" to arrayListOf("tonto", "feo"),
                            "mis tests" to arrayListOf<DatabaseReference>(),
                            "tests realizados" to arrayListOf<DatabaseReference>()
                        )
                        bd.collection("usuarios")
                            .document(auth.currentUser?.uid.orEmpty())
                            .set(usuario)
                            .addOnFailureListener {
                                builder.setMessage(getString(R.string.usuario_info_fail) + it.message).setPositiveButton("OK", dialogClickListener).show()
                                /*Toast.makeText(
                                this,
                                getString(R.string.usuario_info_fail) + it.message,
                                Toast.LENGTH_SHORT
                            ).show()*/ }
                        val intent = Intent(this, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .addOnFailureListener { exception ->
                        builder.setMessage(getString(R.string.creacion_fail) + " ${exception.message}").setPositiveButton("OK", dialogClickListener).show()

                        /*Toast.makeText(
                            baseContext,
                            getString(R.string.creacion_fail) + " ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()*/
                    }
                }
        }
    }
}