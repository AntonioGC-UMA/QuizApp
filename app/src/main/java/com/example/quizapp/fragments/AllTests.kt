package com.example.quizapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.activities.InfoTest
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AllTests : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_tests, container, false)
        val searchTests = view.findViewById<SearchView>(R.id.search_all_tests)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_all_tests)
        //Al cargar este fragmento, se obtienen todos los tests de la BD y se muestran en el fragmento
        //haciendo uso de la clase interna CustomAdapter que para cada test muestra su titulo y descripcion
        Firebase.firestore.collection("tests").get()
            .addOnSuccessListener { documents ->//documents.documents devuelve la lista de document snapshot
                val adapter = CustomAdapter(documents.documents)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = adapter

            }
        //Si el usuario decide realizar una busqueda escribiendo texto en la barra de busqueda,
        //se obtienen las diferentes categorias introducidas por el usuario o el id del test.
        //Si el usuario introduce categorias, se escogen los tests que contengan alguna de las categorias
        //especificadas y se muestran en el fragmento haciendo uso de la clase interna CustomAdapter. Se hace lo
        //mismo cuando el usuario busca por id, con la unica diferencia de que se muestra un solo test, el correspondiente
        //al codigo introducido.
        searchTests.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                val query = query.split(", ").filter { it.isNotEmpty() }.distinct()
                if(query.count() > 0){
                    Firebase.firestore.collection("tests").whereArrayContainsAny("categorias", query).get()
                        .addOnSuccessListener {documents ->
                            Firebase.firestore.collection("tests").document(query[0]).get().addOnSuccessListener {
                                if (it.exists()) {
                                    val adapter = CustomAdapter(listOf(it))
                                    recyclerView.layoutManager = LinearLayoutManager(activity)
                                    recyclerView.adapter = adapter
                                } else {
                                    val adapter = CustomAdapter(documents.documents)
                                    recyclerView.layoutManager = LinearLayoutManager(activity)
                                    recyclerView.adapter = adapter
                                }
                            }
                        }
                }
                return false
            }
        })
        return view
    }
    //Clase interna para mostrar la informacion de una lista de tests, para cada uno se muestra el
    // titulo y la descripcion
    inner class CustomAdapter(private val dataSet: List<DocumentSnapshot>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val itemTitle : TextView = itemView.findViewById(R.id.test_name)
            val itemDescription : TextView = itemView.findViewById(R.id.test_description)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            //Creacion de una nueva vista
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_my_tests_layout, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            //Al mostrar el titulo y la descripcion del test, se añade un controlador para que al
            //pulsar el test se pueda ver la informacion de este y darle al usuario la posibilidad de
            //realizarlo
            viewHolder.itemTitle.text = dataSet[position].get("titulo") as String
            viewHolder.itemDescription.text = dataSet[position].get("descripcion") as String
            viewHolder.view.setOnClickListener{
                val intent = Intent(viewHolder.view.context, InfoTest::class.java)
                SingletonMap["lastTest"] = dataSet[position]
                viewHolder.view.context.startActivity(intent)
            }
        }

        override fun getItemCount() = dataSet.size
    }
}