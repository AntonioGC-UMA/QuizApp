package com.example.quizapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
    //Codigo necesario para mostrar un AlertDialog
    val dialogClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                }
            }
        }
    //Comprueba los datos introducidos por el usuario. Si no se rellena el email o la contraseña no cumple
    //los requisitos se muestra el correspondiente AlertDialog informando de dicho error. En otro caso
    //se obtienen el email y contraseña introducidos
    fun readData() : Pair<String, String>? {
        val email = emailText?.text.toString().trim()
        val password = passwordText?.text.toString().trim()
        val builder = AlertDialog.Builder(this)

        if (email.isEmpty()) {
            builder.setMessage(getString(R.string.email_no_vacio)).setPositiveButton("OK", dialogClickListener).show()
            return null
        }
        if (password.length < 6) {
            builder.setMessage(getString(R.string.pass_min_6)).setPositiveButton("OK", dialogClickListener).show()
            return null
        }

        return Pair(email, password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        emailText = findViewById(R.id.email)
        passwordText = findViewById(R.id.password)

        val auth = FirebaseAuth.getInstance()
        val bd = Firebase.firestore
        val builder = AlertDialog.Builder(this)

        SingletonMap["BD_AUTH"] = auth
        SingletonMap["BD"] = bd
        //Tras localizar los campos de texto de nuestro interes y obtener las referencias a la BD,
        //si el usuario pulsa el boton de login, se lee lo que el usuario ha introducido en los campos
        //de texto, llamando a la funcion readData y en caso de que la autenticacion con email y contraseña
        //sea exitosa, se muestra un mensaje de confirmacion y se abre la actividad Home, donde el usuario
        //tiene acceso a sus tests, todos los tests y los tests realizados. En caso de que el inicio de sesion
        //falle, se muestra un AlertDialog dando detalles acerca del fallo en la autenticacion
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
                    }
                }

        }
        //si el usuario pulsa el boton de registro, se lee lo que el usuario ha introducido en los campos
        //de texto, llamando a la funcion readData y en caso de que la creacion de un usuario con el email y contraseña
        //proporcionados sea exitosa, se muestra un mensaje de confirmacion, se inicializan sus listas de tests como listas
        // vacias y se crea el nuevo usuario. En caso de fallo, se muestra un alertDialog informando sobre el error.
        // Posteriormente se abre la actividad Home
        findViewById<Button>(R.id.registrar).setOnClickListener {
            readData()?.let { (email, password) ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.usuario_creado_exito), Toast.LENGTH_SHORT).show()
                        val usuario = hashMapOf(
                            "mis tests" to arrayListOf<DatabaseReference>(),
                            "tests realizados" to arrayListOf<DatabaseReference>()
                        )
                        bd.collection("usuarios")
                            .document(auth.currentUser?.uid.orEmpty())
                            .set(usuario)
                            .addOnFailureListener {
                                builder.setMessage(getString(R.string.usuario_info_fail) + it.message).setPositiveButton("OK", dialogClickListener).show()
                            }
                        val intent = Intent(this, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .addOnFailureListener { exception ->
                        builder.setMessage(getString(R.string.creacion_fail) + " ${exception.message}").setPositiveButton("OK", dialogClickListener).show()
                    }
                }
        }
    }
}