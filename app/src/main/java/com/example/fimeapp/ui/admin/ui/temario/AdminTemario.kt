package com.example.fimeapp.ui.admin.ui.temario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.example.fimeapp.db_manager.DBHelper


class AdminTemario : Fragment() {

    companion object {
        fun newInstance() = AdminTemario()
    }

    private val viewModel: TemarioViewModel by viewModels()
    private lateinit var databaseHelper: DBHelper

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TemarioAdapter
    private lateinit var items: List<MyItem>
    private lateinit var searchView: SearchView
    private var plan_id = 0
    private var materia_id = 0
    private var academia_id = 0


    private var plan_name = ""
    private var materia_name= ""
    private var academia_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        databaseHelper = DBHelper(requireContext())


        plan_id = requireArguments().getInt("plan")
        materia_id = requireArguments().getInt("materia")
        academia_id = requireArguments().getInt("academia")

        plan_name = databaseHelper.readName("study_plan", "name","id", plan_id.toString()) ?: ""
        materia_name = databaseHelper.readName("materias", "name","id", materia_id.toString()) ?: ""
        academia_name = databaseHelper.readName("academias", "name","id", academia_id.toString()) ?: ""

        // Read data from the table
        items = databaseHelper.read("temario",columns= arrayOf("id","imagen","materia","titulo","descripcion","imagen_url"),"materia= ?", selectionArgs=arrayOf(materia_id.toString()),orderBy ="titulo").toMyItemList()
        // Print results
        for (row in items) {
            println(row)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_temario_admin, container, false)


        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        val plan_text = view.findViewById<TextView>(R.id.textViewPlan)
        val materia_text = view.findViewById<TextView>(R.id.textViewMateria)
        val academia_text = view.findViewById<TextView>(R.id.textViewAcademia)

        plan_text.text = plan_name
        materia_text.text = materia_name
        academia_text.text = academia_name




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Read data from the table



        // Convert results to List<MyItem>

        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TemarioAdapter(requireContext(), items ,{ item ->
            val bundle = Bundle().apply {
                putInt("temario", item.id)
                putInt("plan", plan_id)
                putInt("materia", materia_id)
                putInt("academia", academia_id)
            }
                findNavController().navigate(R.id.action_adminTemario_to_adminMaterial, bundle)

            }, {
                item ->
                val bundle = Bundle().apply {
                    putInt("temario", item.id)
                    putInt("plan", plan_id)
                    putInt("materia", materia_id)
                    putInt("academia", academia_id)
                }
                findNavController().navigate(R.id.action_adminTemario_to_addTemario, bundle)
        } )


        recyclerView.adapter = adapter


        // Set up the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })


    }


    private fun List<Map<String, Any?>>.toMyItemList(): List<MyItem> {
        return this.map { map ->
            MyItem(
                id = map["id"] as Int,
                title = map["titulo"] as String,
                materia = map["materia"] as Int,
                description = map["descripcion"] as String,
                image =  map["imagen"] ?: ByteArray(0),
                imagen_url =  map["imagen_url"] as String,
            )
        }
    }



}