package com.example.quizapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quizapp.R
import com.example.quizapp.entities.SingletonMap
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        var auth = SingletonMap["BD_AUTH"] as FirebaseAuth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navigationView = findViewById<BottomNavigationView>(R.id.homeMenuNavigationView)
        val navigationController = findNavController(R.id.fragmentContainerView)

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.myTestsFragment, R.id.doneTestsFragment))
        setupActionBarWithNavController(navigationController, appBarConfiguration)

        navigationView.setupWithNavController(navigationController)
    }
}