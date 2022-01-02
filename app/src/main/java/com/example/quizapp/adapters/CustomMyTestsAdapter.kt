package com.example.quizapp.adapters
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.activities.CrearTest
import com.example.quizapp.entities.SingletonMap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CustomMyTestsAdapter(private val list: MutableList<String>): RecyclerView.Adapter<CustomMyTestsAdapter.ViewHolder>() {
    val titles = arrayOf("cosas")
    val descriptions = arrayOf("Descripcion 1", "Descripcion 2", "Descripcion 4")
    val images = intArrayOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background
    )

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_my_tests_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = list[i]
        viewHolder.itemDescription.text = descriptions[i]
        viewHolder.itemImage.setImageResource(images[i])
        viewHolder.itemView.setOnClickListener{
            val intent = Intent(viewHolder.itemView.context, CrearTest::class.java)
            intent.putExtra("id", list[i])
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }
    inner class ViewHolder(val itemView : View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemTitle : TextView
        var itemDescription : TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.test_name)
            itemDescription = itemView.findViewById(R.id.test_description)

        }
    }
}


