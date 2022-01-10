package com.example.quizapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R
import com.example.quizapp.fragments.MyTestsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.quizapp.fragments.AllTests
import com.example.quizapp.fragments.DoneTestsFragment

class Home : AppCompatActivity() {


    private var toolbar: ActionBar? = null
    lateinit var bottomNavigationView: BottomNavigationView
    //Al crear la actividad home, se enlaza la barra de navegacion inferior con los distintos fragmentos
    //Por defecto, se muestra el fragmento AllTests y se modifica el titulo de la toolbar segun se
    //muestre un fragmento u otro
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = supportActionBar
        bottomNavigationView =
            findViewById<View>(R.id.homeMenuNavigationView) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        toolbar?.title = "QuizApp"
        loadFragment(AllTests())
    }

    //Dependendiendo del elemento seleccionado de la barra de navegacion inferior se muestra un fragmento
    //u otro. Para mostrar el fragmento se recurre a la funcion loadFragment, que se describe mas abajo
    private val navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.allTestsFragment -> {
                    toolbar?.title = "QuizApp"
                    fragment = AllTests()
                    loadFragment(fragment)
                    true
                }
                R.id.myTestsFragment -> {
                    toolbar?.title = getString(R.string.my_tests_title_fragment)
                    fragment = MyTestsFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.doneTestsFragment -> {
                    toolbar?.title = getString(R.string.tests_done_title_fragment)
                    fragment = DoneTestsFragment()
                    loadFragment(fragment)
                    true
                }

            }
            false
        }

    //Dado un fragmento, cuando esta funcion es invocada se comienza una nueva transaccion donde se
    //reemplaza el contenido del frame layout por el del fragmento pasado como parametro y se
    //hace commit de la transaccion, esto es, que se pone en cola dicha peticion hasta ser atendida
    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}