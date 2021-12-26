package com.example.quizapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class CustomMyTestsAdapter: RecyclerView.Adapter<CustomMyTestsAdapter.ViewHolder>() {
    val titles = arrayOf("Titulo 1", "Titulo 2", "Titulo 4")
    val descriptions = arrayOf("Descripcion 1", "Descripcion 2", "Descripcion 4")
    val images = intArrayOf(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background, R.drawable.ic_launcher_background)
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_my_tests_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemDescription.text = descriptions[i]
        viewHolder.itemImage.setImageResource(images[i])
    }

    override fun getItemCount(): Int {
        return titles.size
    }
    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemTitle : TextView
        var itemDescription : TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.test_description)
            itemDescription = itemView.findViewById(R.id.test_description)

        }
    }
}