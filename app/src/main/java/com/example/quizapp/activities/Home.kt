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

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    /*override fun onCreate(savedInstanceState: Bundle?) {
        var auth = SingletonMap["BD_AUTH"] as FirebaseAuth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navigationView = findViewById<BottomNavigationView>(R.id.homeMenuNavigationView)
        val navigationController = findNavController(R.id.fragmentContainerView)

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.allTestsFragment, R.id.myTestsFragment, R.id.doneTestsFragment))
        setupActionBarWithNavController(navigationController, appBarConfiguration)

        navigationView.setupWithNavController(navigationController)

        val searchTests = findViewById<SearchView>(R.id.searchTest)
        searchTests.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                val query = query.split(", ").filter { it.isNotEmpty() }.distinct()
                println(query)
                if(query.count() > 0){
                    Firebase.firestore.collection("tests").whereArrayContainsAny("categorias", query).get()
                        .addOnSuccessListener {documents ->
                            val resultados = documents.map { it.id }
                            println(resultados)
                            val tag = "FRAGMENT_MY_TESTS"
                            var fragment = supportFragmentManager.findFragmentByTag(tag)
                            if (fragment != null){ 
                                supportFragmentManager.beginTransaction().remove(fragment).commit()
                                supportFragmentManager.popBackStack()
                            }
                            fragment = MyTestsFragment.newInstance(resultados)
                            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView,fragment, tag).commit()
                            
                    }
                }
                return false
            }

        })


    }*/
}