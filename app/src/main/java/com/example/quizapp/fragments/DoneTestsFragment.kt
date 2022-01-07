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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoneTestsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_done_tests, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMyTestsDone)
        val search = view.findViewById<SearchView>(R.id.search_done_tests)
        val user_id = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val user = Firebase.firestore.collection("usuarios").document(user_id)
        user.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val tests = document.data?.get("tests realizados") as List<DocumentReference>
                    val tasks = tests.map { it.get() }
                    Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                        .addOnSuccessListener{ list -> //Do what you need to do with your list
                            val adapter = CustomAdapter(list)
                            recyclerView.layoutManager = LinearLayoutManager(activity)
                            recyclerView.adapter = adapter
                        }
                }
            }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                val query = query.split(", ").filter { it.isNotEmpty() }.distinct()
                println(query)
                if(query.count() > 0){
                    Firebase.firestore.collection("usuarios").document(user_id)
                        .get().addOnSuccessListener { documento ->
                            val tests = documento.get("tests realizados") as List<DocumentReference>
                            val tasks = tests.map { it.get() }
                            Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                                .addOnSuccessListener{ list -> //Do what you need to do with your list
                                   val lista_filtrada = list.filter {
                                        (it.get("categorias") as List<String>).containsAll(query)  ||
                                                it.id == query[0]
                                    }
                                    val adapter = CustomAdapter(lista_filtrada)
                                    recyclerView.layoutManager = LinearLayoutManager(activity)
                                    recyclerView.adapter = adapter
                                }
                        }


                }
                return false
            }
        })
        return view
    }


    // Esto es del recycler view
    inner class CustomAdapter(private val dataSet: List<DocumentSnapshot>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val itemTitle : TextView = itemView.findViewById(R.id.test_name)
            val itemDescription : TextView = itemView.findViewById(R.id.test_description)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_my_tests_layout, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemTitle.text = dataSet[position].get("titulo") as String
            viewHolder.itemDescription.text = dataSet[position].get("descripcion") as String
            viewHolder.view.setOnClickListener{
                val intent = Intent(viewHolder.view.context, InfoTest::class.java)
                SingletonMap["lastTest"] = dataSet[position]
                viewHolder.view.context.startActivity(intent)
                //TODO: Abrir una activity para rehacer el test
            }
        }

        override fun getItemCount() = dataSet.size
    }
}