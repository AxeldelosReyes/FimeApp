package com.example.fimeapp.ui.temario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.example.fimeapp.db_manager.DBHelper

class temario : Fragment() {

    companion object {
        fun newInstance() = temario()
    }

    private val viewModel: TemarioViewModel by viewModels()
    private lateinit var databaseHelper: DBHelper

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TemarioAdapter
    private lateinit var items: List<MyItem>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DBHelper(requireContext())

        // Read data from the table
        val items = databaseHelper.read("study_plan", arrayOf("id", "name"))


        // Print results
        for (row in items) {
            println(row)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_temario, container, false)
        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Read data from the table
        val itemsFromDb = databaseHelper.read("study_plan", arrayOf("id", "name"))

        // Convert results to List<MyItem>
        items = itemsFromDb.toMyItemList()

        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TemarioAdapter(requireContext(), items)
        recyclerView.adapter = adapter
    }


    private fun List<Map<String, Any?>>.toMyItemList(): List<MyItem> {
        return this.map { map ->
            MyItem(
                title = map["id"].toString() as String,
                description = map["name"] as String,
                image =  ByteArray(0)
            )
        }
    }



}