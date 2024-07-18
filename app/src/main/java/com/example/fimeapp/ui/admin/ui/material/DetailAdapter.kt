package com.example.fimeapp.ui.admin.ui.material

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.example.fimeapp.ui.admin.ui.temario.MyItem
import com.squareup.picasso.Picasso

data class DetailItem(
    val id: String,
    val name: String,
    val temario_id: String,
    val tipo: String,
    val external_link: String,
    var like: Boolean
)


class DetailAdapter (
    private val context: Context,
    private val items: List<DetailItem>,
    private val itemClickListener: (DetailItem) -> Unit,
    private val toggleFavorite: (DetailItem) -> Unit
    ) : RecyclerView.Adapter<DetailAdapter.ViewHolder>(), Filterable {

        private var itemsFiltered: List<DetailItem> = items

        // ViewHolder class to hold item views
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescriptionMaterial)
            val imageView: ImageView = itemView.findViewById(R.id.imageViewMaterial)
            val CircleView: View = itemView.findViewById(R.id.circleView)
            val love: View = itemView.findViewById(R.id.love)

            fun bind(item: DetailItem, position: Int) {
                textViewDescription.text = item.name
                textViewDescription.setOnClickListener { itemClickListener(item) }
                imageView.setOnClickListener { itemClickListener(item) }
                CircleView.setOnClickListener { itemClickListener(item) }
                love.setOnClickListener {
                    item.like = !item.like
                    toggleFavorite(item)
                    notifyItemChanged(position)
                }
                // Update love view based on item's like status
                love.setBackgroundResource(if (item.like) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24)

                val imageUrl = when (item.tipo) {
                    "pdf" -> R.drawable.icon_pdf
                    "video" -> R.drawable.icon_video
                    else -> null
                }

                imageUrl?.let {
                    Picasso.get()
                        .load(it)
                        .resize(100, 100)
                        .centerInside()
                        .into(imageView)
                }

            }
        }

        // Inflate the item layout and create the ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.material_item_admin, parent, false)
            return ViewHolder(view)
        }

        // Bind data to the item views
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemsFiltered[position]
            holder.bind(item,position)
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

    fun updateItems(newItems: List<DetailItem>) {
        itemsFiltered = newItems
        notifyDataSetChanged()
    }
}