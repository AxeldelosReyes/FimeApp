package com.example.fimeapp.ui.admin.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.example.fimeapp.R
import com.example.fimeapp.db_manager.DBHelper

data class SpinnerItem(val id: Int, val name: String)

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<SpinnerItem>,
    private val iconResId: Int,
) : ArrayAdapter<SpinnerItem>(context, android.R.layout.simple_spinner_item, items) {

    private var filteredItems: List<SpinnerItem> = items

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.spinner_item_with_icon, parent, false)

        val icon = view.findViewById<ImageView>(R.id.icon)
        val text = view.findViewById<TextView>(R.id.text)
        icon.setImageResource(iconResId)
        text.text = filteredItems[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Use the default spinner item layout for dropdown items
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)

        val text = view.findViewById<TextView>(android.R.id.text1)
        text.text = filteredItems[position].name

        return view
    }

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): SpinnerItem? {
        return filteredItems[position]
    }

    override fun getPosition(item: SpinnerItem?): Int {
        return filteredItems.indexOf(item)
    }

    fun updateItems(newItems: List<SpinnerItem>) {
        filteredItems = newItems
        notifyDataSetChanged()
    }
    override fun isEnabled(position: Int): Boolean {
        return if (position == 0) {
            // Disable the first item from Spinner
            // First item will be use for hint
            false
        } else {
            true
        }
    }
}
