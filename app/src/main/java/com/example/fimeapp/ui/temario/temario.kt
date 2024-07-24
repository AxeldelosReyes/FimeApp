package com.example.fimeapp.ui.temario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class temario : Fragment() {

    companion object {
        fun newInstance() = temario()
    }

    private val viewModel: TemarioViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TemarioAdapter
    private lateinit var items: List<MyItem>
    private lateinit var searchView: SearchView
    private var plan_id: String = ""
    private var materia_id: String = ""
    private var academia_id: String = ""


    private var plan_name = ""
    private var materia_name = ""
    private var academia_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plan_id = requireArguments().getString("plan_id").toString()
        materia_id = requireArguments().getString("materia_id").toString()
        academia_id = requireArguments().getString("academia_id").toString()


        plan_name = requireArguments().getString("plan_name").toString()
        materia_name = requireArguments().getString("materia_name").toString()
        academia_name = requireArguments().getString("academia_name").toString()


        items = mutableListOf<Map<String, Any?>>().toMyItemList()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_temario, container, false)


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


        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TemarioAdapter(requireContext(), items, { item ->
            val bundle = Bundle().apply {
                putString("temario_id", item.id)
                putString("plan_id", plan_id)
                putString("academia_id", academia_id)
                putString("materia_id", materia_id)
                putString("plan_name", plan_name)
                putString("academia_name", academia_name)
                putString("materia_name", materia_name)
            }
            findNavController().navigate(R.id.action_temario_to_material, bundle)

        }, { item ->
            val bundle = Bundle().apply {
                putString("temario_id", item.id)
                putString("plan_id", plan_id)
                putString("academia_id", academia_id)
                putString("materia_id", materia_id)
                putString("plan_name", plan_name)
                putString("academia_name", academia_name)
                putString("materia_name", materia_name)
            }
            findNavController().navigate(R.id.action_adminTemario_to_addTemario, bundle)
        })


        recyclerView.adapter = adapter

        fetch_from_firebase_database("temario")


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
                id = map["id"] as String,
                title = map["name"] as String,
                materia = map["materia"] as String,
                description = map["descripcion"] as String,
                image = map["imagen"] ?: ByteArray(0),
                imagen_url = map["imagen_url"] as String,
            )
        }
    }


    private fun fetch_from_firebase_database(table: String) {

        val database = Firebase.firestore
        val results = mutableListOf<Map<String, Any?>>()

        when (table) {
            "temario" -> {
                database.collection("temario")
                    .whereEqualTo("materia", database.collection("materias").document(materia_id))
                    .orderBy("name")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val data = hashMapOf(
                                "id" to document.id,
                                "name" to document.data["name"] as String,
                                "materia" to materia_id,
                                "descripcion" to document.data["descripcion"] as String,
                                "imagen_url" to document.data["imagen_url"] as String,
                            )
                            results.add(data)
                        }
                        adapter.updateItems(results.toMyItemList())
                    }.addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
        }


    }


}