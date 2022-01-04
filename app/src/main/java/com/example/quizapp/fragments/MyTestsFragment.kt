package com.example.quizapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.activities.CrearTest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList
import com.google.firebase.firestore.DocumentSnapshot

import com.google.android.gms.tasks.OnSuccessListener

import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.whenAllSuccess


import com.google.firebase.auth.FirebaseUser




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyTestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyTestsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val tests = arguments?.getStringArrayList(ARG_NAME)
        val view = inflater.inflate(R.layout.fragment_my_tests, container, false)
        val buttonCreateTest = view.findViewById<FloatingActionButton>(R.id.createTestButton)
        buttonCreateTest.setOnClickListener {
            val intent = Intent(activity, CrearTest::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent) }
        val search = view.findViewById<SearchView>(R.id.search_my_tests)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMyTests)
        val user_id = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val user = Firebase.firestore.collection("usuarios").document(user_id)
        user.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val tests = document.data?.get("mis tests") as List<DocumentReference>
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
                            val tests = documento.get("mis tests") as List<DocumentReference>
                            val tasks = tests.map { it.get() }
                            Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                                .addOnSuccessListener{ list -> //Do what you need to do with your list
                                    val lista_filtrada = list.filter {
                                        (it.get("categorias") as List<String>).containsAll(query)
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

    companion object {
        const val ARG_NAME = "tests"
        fun newInstance(name: List<String>): MyTestsFragment {
            val fragment = MyTestsFragment()
            println(name)
            val bundle = Bundle().apply {
                putStringArrayList(ARG_NAME, name as ArrayList<String>)
            }

            fragment.arguments = bundle

            return fragment
        }
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
                val intent = Intent(viewHolder.view.context, CrearTest::class.java)
                intent.putExtra("id", dataSet[position].id)
                viewHolder.view.context.startActivity(intent)
            }
        }

        override fun getItemCount() = dataSet.size
    }
}