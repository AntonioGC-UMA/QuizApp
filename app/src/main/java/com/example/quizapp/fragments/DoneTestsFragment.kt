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
import com.example.quizapp.activities.CrearTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoneTestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoneTestsFragment : Fragment() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var textTestsDone : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

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
                    val adapter = CustomAdapter(tests.map { it.id })
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = adapter
                } else {
                }
            }
            .addOnFailureListener { exception ->
               println("algo")
            }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                val query = query.split(", ").filter { it.isNotEmpty() }.distinct()
                println(query)
                if(query.count() > 0){ //Firebase.firestore.collection("tests").whereArrayContainsAny("categorias", query).get()
                    Firebase.firestore.collection("usuarios").document(user_id).collection("tests realizados")
                        .whereArrayContainsAny("categorias", query).get()
                        .addOnSuccessListener {//TODO: Hay que ponerlo para que una vez tenga los tests del usuario los pueda filtrar
                            documents ->
                            val resultados = documents.map { it.id }
                            println(resultados)
                            val adapter = CustomAdapter(resultados)
                            recyclerView.layoutManager = LinearLayoutManager(activity)
                            recyclerView.adapter = adapter
                        }
                }
                return false
            }
        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoneTestsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoneTestsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // Esto es del recycler view
    inner class CustomAdapter(private val dataSet: List<String>) :
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
            viewHolder.itemTitle.text = dataSet[position]
            viewHolder.itemDescription.text = dataSet[position]
            viewHolder.view.setOnClickListener{
                /*val intent = Intent(viewHolder.view.context, CrearTest::class.java)
                intent.putExtra("id", dataSet[position])
                viewHolder.view.context.startActivity(intent)
                */
                 //TODO: Abrir una activity para rehacer el test
            }
        }

        override fun getItemCount() = dataSet.size
    }
}