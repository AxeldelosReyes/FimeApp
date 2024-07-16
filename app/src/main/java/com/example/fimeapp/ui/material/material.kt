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

        databaseHelper = DBHelper(requireContext())


        val temario_id = requireArguments().getInt("temario")


        // Read data from the table
        items = databaseHelper.read("material", arrayOf("id",
            "tipo","name","external_link","temario_id"),"temario_id= ?", arrayOf(temario_id.toString())).toMyItemList()

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

    private fun List<Map<String, Any?>>.toMyItemList(): List<DetailItem> {
        return this.map { map ->
            DetailItem(
                id = map["id"] as Int,
                name = map["name"] as String,
                external_link = (map["external_link"] ?: "").toString(),
                tipo = map["tipo"] as String,
                temario_id = map["temario_id"] as Int,
            )
        }
    }

}