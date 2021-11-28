package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    var email : EditText? = null
    var password : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById<EditText>(R.id.email)
        password = findViewById<EditText>(R.id.password)

        findViewById<Button>(R.id.login).setOnClickListener {
            val e = email?.text.toString()
            val p = password?.text.toString()

            val auth = FirebaseAuth.getInstance()

            if (!TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) {
                auth.signInWithEmailAndPassword(e, p).addOnCompleteListener {
                    task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Autenticacion correcta", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Autenticacion fallida", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        findViewById<Button>(R.id.registrar).setOnClickListener {
            val e = email?.text.toString()
            val p = password?.text.toString()

            val auth = FirebaseAuth.getInstance()

            if (!TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) {
                auth.createUserWithEmailAndPassword(e, p)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(baseContext, "Creacion fallida.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }


}