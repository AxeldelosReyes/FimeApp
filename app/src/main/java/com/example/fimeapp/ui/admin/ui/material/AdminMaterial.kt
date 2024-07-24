package com.example.fimeapp.ui.admin.ui.material

import android.content.ContentValues
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore


class AdminMaterial : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetailAdapter
    private lateinit var items: List<DetailItem>
    private lateinit var searchView: SearchView
    private lateinit var noFilesText: TextView

    private var current_user: FirebaseUser? = null
    private var plan_id: String = ""
    private var temario_id: String = ""
    private var materia_id: String = ""
    private var academia_id: String = ""


    private var plan_name = ""
    private var materia_name = ""
    private var academia_name = ""


    companion object {
        fun newInstance() = AdminMaterial()
    }

    private val viewModel: MaterialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        current_user = Firebase.auth.currentUser

        temario_id = requireArguments().getString("temario_id").toString()
        plan_id = requireArguments().getString("plan_id").toString()
        materia_id = requireArguments().getString("materia_id").toString()
        academia_id = requireArguments().getString("academia_id").toString()


        plan_name = requireArguments().getString("plan_name").toString()
        materia_name = requireArguments().getString("materia_name").toString()
        academia_name = requireArguments().getString("academia_name").toString()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_material_admin, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDetail)
        searchView = view.findViewById(R.id.searchViewMaterial)
        noFilesText = view.findViewById(R.id.noFilesText)

        val iconAdd = view.findViewById<ImageView>(R.id.iconAdd)

        iconAdd.setOnClickListener {
            val bundle = Bundle().apply {
                putString("temario_id", temario_id)
                putString("plan_id", plan_id)
                putString("academia_id", academia_id)
                putString("materia_id", materia_id)
                putString("plan_name", plan_name)
                putString("academia_name", academia_name)
                putString("materia_name", materia_name)
            }
            findNavController().navigate(R.id.action_adminMaterial_to_createMaterial, bundle)


        }


        items = emptyList()

        fetch_from_firebase_database("material")

        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val plan_text = view.findViewById<TextView>(R.id.textViewPlan)
        val materia_text = view.findViewById<TextView>(R.id.textViewMateria)
        val academia_text = view.findViewById<TextView>(R.id.textViewAcademia)

        plan_text.text = plan_name
        materia_text.text = materia_name
        academia_text.text = academia_name



        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DetailAdapter(requireContext(), items, { item ->
            // Handle item click
            if (item.tipo == "pdf") {
                if (item.external_link.isNotEmpty()) {
                    startActivity(
                        PdfViewerActivity.launchPdfFromUrl(
                            context = this.context, pdfUrl = item.external_link,
                            pdfTitle = item.name, saveTo = saveTo.ASK_EVERYTIME,
                            enableDownload = true
                        )
                    )
                }
            } else if (item.tipo == "video") {
                findNavController().navigate(R.id.action_adminMaterial_to_youTubePlayerFragment2)
            }
        }, { item ->
            // Handle toggle favorite
            this.toggleFavorite(item)
        })
        recyclerView.adapter = adapter


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

    private fun List<Map<String, Any?>>.toMyItemList(): List<DetailItem> {
        return this.map { map ->
            DetailItem(
                id = map["id"] as String,
                name = map["name"] as String,
                external_link = (map["external_link"] ?: "").toString(),
                tipo = map["tipo"] as String,
                temario_id = map["temario_id"] as String,
                like = false,
            )
        }
    }


    private fun isFavorite(item: DetailItem) {
        val db = Firebase.firestore
        var isFavorite = false
        val query = db.collection("favoritos")
            .whereEqualTo("material_id", item.id)
            .whereEqualTo("uuid", current_user?.uid)
            .get()
            .addOnSuccessListener { doc ->
                isFavorite = doc.size() > 0
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        item.like = isFavorite
    }

    fun toggleFavorite(item: DetailItem) {
        val db = Firebase.firestore
        if (item.like) {
            val update = hashMapOf(
                "material_id" to db.collection("material").document(item.id),
                "uuid" to current_user?.uid,
            )
            db.collection("favoritos")
                .add(update)
                .addOnSuccessListener { documentReference ->
                    Log.d("FirestoreAdd", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("FirestoreAdd", "Error adding document", e)
                }

        } else {
            db.collection("favoritos")
                .whereEqualTo("material_id", db.collection("material").document(item.id))
                .whereEqualTo("uuid", current_user?.uid)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        db.collection("favoritos").document(document.id).delete()
                    }
                }
        }
    }


    private fun fetch_from_firebase_database(table: String) {
        val database = Firebase.firestore
        val results = mutableListOf<Map<String, Any?>>()

        when (table) {
            "material" -> {
                database.collection("material")
                    .whereEqualTo("temario_id", database.collection("temario").document(temario_id))
                    .orderBy("name")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val data = hashMapOf(
                                "id" to document.id,
                                "name" to document.data["name"] as String,
                                "tipo" to document.data["tipo"] as String,
                                "temario_id" to temario_id,
                                "external_link" to document.data["external_link"] as String,
                            )
                            results.add(data)
                        }

                        if (results.isEmpty()) {
                            noFilesText.visibility = View.VISIBLE
                        } else {
                            noFilesText.visibility = View.GONE
                            val new_list = results.toMyItemList()

                            new_list.forEach { item ->
                                database.collection("favoritos")
                                    .whereEqualTo("material_id", database.collection("material").document(item.id))
                                    .whereEqualTo("uuid", current_user?.uid)
                                    .get()
                                    .addOnSuccessListener { doc ->
                                        item.like = doc.size() > 0
                                        adapter.updateItems(new_list)
                                        items = new_list
                                    }
                                    .addOnFailureListener { exception ->
                                        item.like = false
                                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
        }
    }

}