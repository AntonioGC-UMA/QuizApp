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
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.whenAllSuccess
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class MyTestsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_tests, container, false)
        val buttonCreateTest = view.findViewById<FloatingActionButton>(R.id.createTestButton)
        buttonCreateTest.setOnClickListener {
            val intent = Intent(activity, CrearTest::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent) }
        val search = view.findViewById<SearchView>(R.id.search_my_tests)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMyTests)
        //Al cargar este fragmento, se obtienen los tests creados por el usuario actual y se muestran en el fragmento
        //haciendo uso de la clase interna CustomAdapter que para cada test muestra su titulo y descripcion
        val user_id = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val user = Firebase.firestore.collection("usuarios").document(user_id)
        user.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val tests = document.data?.get("mis tests") as List<DocumentReference>
                        val tasks = tests.map { it.get() }
                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                            .addOnSuccessListener{ list ->
                                val adapter = CustomAdapter(list)
                                recyclerView.layoutManager = LinearLayoutManager(activity)
                                recyclerView.adapter = adapter
                            }
                    }
                }
        //Si el usuario decide realizar una busqueda escribiendo texto en la barra de busqueda,
        //se obtienen las diferentes categorias introducidas por el usuario o el id del test.
        //Si el usuario introduce categorias, se escogen aquellos tests creados por el usuario
        // que contengan alguna de las categorias especificadas y se muestran en el fragmento
        // haciendo uso de la clase interna CustomAdapter. Se hace lo mismo cuando el usuario busca
        // por id, con la unica diferencia de que se muestra un solo test, el correspondiente
        //al codigo introducido.
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                val query = query.split(", ").filter { it.isNotEmpty() }.distinct()
                println(query)
                if(query.count() > 0){
                    Firebase.firestore.collection("usuarios").document(user_id)
                        .get().addOnSuccessListener { documento ->
                            val tests = documento.get("mis tests") as List<DocumentReference>
                            val tasks = tests.map { it.get() }
                            whenAllSuccess<DocumentSnapshot>(tasks)
                                .addOnSuccessListener{ list ->
                                    val lista_filtrada = list.filter {
                                        (it.get("categorias") as List<String>).containsAll(query) ||
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
            //pulsar el test, este se pueda editar. Para abrir la actividad que permite editar el test
            // es necesario incluir su id en los parametros extras de la nueva actividad
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