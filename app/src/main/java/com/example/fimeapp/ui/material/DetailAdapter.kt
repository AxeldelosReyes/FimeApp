package com.example.fimeapp.ui.material

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R

data class DetailItem(
    val id: Int,
    val name: String,
    val temario_id: Int,
    val tipo: String,
    val uri: String,
    val asset: String,
    val external_link: String,
)


class DetailAdapter (
    private val context: Context,
    private val items: List<DetailItem>,
    private val itemClickListener: (DetailItem) -> Unit
    ) : RecyclerView.Adapter<DetailAdapter.ViewHolder>(), Filterable {

        private var itemsFiltered: List<DetailItem> = items

        // ViewHolder class to hold item views
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescriptionMaterial)

            fun bind(item: DetailItem) {
                textViewDescription.text = item.name
                itemView.setOnClickListener { itemClickListener(item) }
            }
        }

        // Inflate the item layout and create the ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.material_item, parent, false)
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
                        it.name.lowercase().contains(query)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                itemsFiltered = filterResults?.values as List<DetailItem>
                notifyDataSetChanged()
            }
        }
    }
}