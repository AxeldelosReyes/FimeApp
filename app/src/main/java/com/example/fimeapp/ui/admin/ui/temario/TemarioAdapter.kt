package com.example.fimeapp.ui.admin.ui.temario

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.squareup.picasso.Picasso
import com.example.fimeapp.ui.admin.ui.home.SpinnerItem

data class MyItem(
    val id : String,
    val materia: String,
    val title: String,
    val description: String,
    val image: Any,
    val imagen_url: String
)


class TemarioAdapter (
    private val context: Context,
    private val items: List<MyItem>,
    private val itemClickListener: (MyItem) -> Unit,
    private val addButtonClickListener: (MyItem) -> Unit
    ) : RecyclerView.Adapter<TemarioAdapter.ViewHolder>(), Filterable {

        private var itemsFiltered: List<MyItem> = items

        // ViewHolder class to hold item views
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
            val linearLayout : LinearLayout = itemView.findViewById(R.id.linearLayout)
            val addButton: View = itemView.findViewById(R.id.add_btn)

            fun bind(item: MyItem) {
                textViewTitle.text = item.title
                textViewDescription.text = item.description
                addButton.setOnClickListener { addButtonClickListener(item) }
                linearLayout.setOnClickListener { itemClickListener(item) }
                if (item.imagen_url.isNotEmpty()){
                    Picasso.get()
                        .load(item.imagen_url)
                        .into(imageView)
                }


            }
        }

        // Inflate the item layout and create the ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.temario_item_admin, parent, false)
            return ViewHolder(view)
        }

        // Bind data to the item views
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemsFiltered[position]
            holder.bind(item)
        }

        // Return the total number of items
        override fun getItemCount(): Int {
            return itemsFiltered.size
        }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val query = charSequence?.toString()?.lowercase() ?: ""

                itemsFiltered = if (query.isEmpty()) {
                    items
                } else {
                    items.filter {
                        it.title.lowercase().contains(query) || it.description.lowercase().contains(query)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                itemsFiltered = filterResults?.values as List<MyItem>
                notifyDataSetChanged()
            }
        }
    }

    fun updateItems(newItems: List<MyItem>) {
        itemsFiltered = newItems
        notifyDataSetChanged()
    }

}