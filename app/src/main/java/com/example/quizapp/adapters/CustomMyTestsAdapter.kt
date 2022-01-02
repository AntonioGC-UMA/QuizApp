package com.example.quizapp.adapters
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.activities.CrearTest

class CustomMyTestsAdapter(private val list: ArrayList<String>): RecyclerView.Adapter<CustomMyTestsAdapter.ViewHolder>() {
    val titles = arrayOf("cosas")
    val descriptions = arrayOf("Descripcion 1", "Descripcion 2", "Descripcion 4")
    val images = intArrayOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background
    )
    inner class ViewHolder(val view : View): RecyclerView.ViewHolder(view){
        val itemImage : ImageView = itemView.findViewById(R.id.item_image)
        val itemTitle : TextView = itemView.findViewById(R.id.test_name)
        val itemDescription : TextView = itemView.findViewById(R.id.test_description)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_my_tests_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = list[i]
        viewHolder.itemDescription.text = descriptions[i]
        viewHolder.itemImage.setImageResource(images[i])
        viewHolder.view.setOnClickListener{
            val intent = Intent(viewHolder.view.context, CrearTest::class.java)
            intent.putExtra("id", list[i])
            viewHolder.view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

}


