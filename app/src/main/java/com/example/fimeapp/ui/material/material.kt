package com.example.fimeapp.ui.material

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.example.fimeapp.db_manager.DBHelper
import com.example.fimeapp.ui.temario.MyItem


class material : Fragment() {

    private lateinit var databaseHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetailAdapter
    private lateinit var items: List<DetailItem>
    private lateinit var searchView: SearchView

    companion object {
        fun newInstance() = material()
    }

    private val viewModel: MaterialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = requireArguments().getString("title")
        val description = requireArguments().getString("description")

        println("Title:${title}")
        println("description:${description}")
        items = listOf(DetailItem(title ?: "", description ?: "",  byteArrayOf(0)))
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_material, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDetail)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DetailAdapter(requireContext(), items){ item ->
            println("material:${item}")
        }
        recyclerView.adapter = adapter
    }

}