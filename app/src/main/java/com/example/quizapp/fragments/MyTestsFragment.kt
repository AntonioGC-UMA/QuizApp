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
                        val adapter = CustomAdapter(tests.map { it.id })
                        recyclerView.layoutManager = LinearLayoutManager(activity)
                        recyclerView.adapter = adapter
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
                if(query.count() > 0){ //Firebase.firestore.collection("tests").whereArrayContainsAny("categorias", query).get()
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid
                        Firebase.firestore.collection("usuarios").document(uid).get()
                            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
                                if (task.isSuccessful) {
                                    val document = task.getResult()
                                    if (document != null) {
                                        if (document.exists()) {
                                            val list = document["mis tests"] as List<DocumentReference>?
                                            val tasks = mutableListOf<Task<DocumentSnapshot>>()
                                            for (documentReference in list!!) {
                                                val documentSnapshotTask: Task<DocumentSnapshot> =
                                                    documentReference.get()
                                                tasks.add(documentSnapshotTask)
                                            }
                                            Tasks.whenAllSuccess<Task<List<Any>>>(tasks)
                                                .addOnSuccessListener{ list -> //Do what you need to do with your list
                                                    println(list)
                                                    /*for (obj in list) {
                                                        //println("TAG " + (obj as DocumentSnapshot).data?.get("descripcion"))
                                                        println("TAG " + obj)
                                                    }*/
                                                }
                                        }
                                    }
                                }
                            })
                    }




                    /*
                    val coleccion = Firebase.firestore.collection("usuarios").document(user_id).collection("mis tests")

                    println(coleccion.path)
                    coleccion.whereArrayContainsAny("categorias", query).get()
                        .addOnSuccessListener {//TODO: Hay que ponerlo para que una vez tenga los tests del usuario los pueda filtrar
                                documents ->
                            val resultados = documents.map { it.id }
                            println(resultados)
                            val adapter = CustomAdapter(resultados)
                            recyclerView.layoutManager = LinearLayoutManager(activity)
                            recyclerView.adapter = adapter
                        }

                     */
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
                val intent = Intent(viewHolder.view.context, CrearTest::class.java)
                intent.putExtra("id", dataSet[position])
                viewHolder.view.context.startActivity(intent)
            }
        }

        override fun getItemCount() = dataSet.size
    }
}