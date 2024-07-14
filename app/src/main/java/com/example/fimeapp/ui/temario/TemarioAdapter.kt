package com.example.fimeapp.ui.temario

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R

data class MyItem(
    val title: String,
    val description: String,
    val image: ByteArray
)


class TemarioAdapter (
    private val context: Context,
    private val items: List<MyItem>
    ) : RecyclerView.Adapter<TemarioAdapter.ViewHolder>() {

        // ViewHolder class to hold item views
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        }

        // Inflate the item layout and create the ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.temario_item, parent, false)
            return ViewHolder(view)
        }

        // Bind data to the item views
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.textViewTitle.text = item.title
            holder.textViewDescription.text = item.description
        }

        // Return the total number of items
        override fun getItemCount(): Int {
            return items.size
        }
}