package com.example.fimeapp.ui.admin.ui.favoritos

import android.content.ContentValues
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
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
import com.example.fimeapp.ui.admin.ui.material.MaterialViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo


data class FavoritoDB(
    val id: String,
    val uuid: String,
    val material_id: Any,
)



class AdminFavoritos : Fragment() {

    companion object {
        fun newInstance() = AdminFavoritos()
    }

    private lateinit var databaseHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritosAdapter
    private lateinit var items: List<FavDetailItem>
    private lateinit var searchView: SearchView

    private var current_user: FirebaseUser? = null

    private val viewModel: MaterialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DBHelper(requireContext())

        current_user = Firebase.auth.currentUser

        val materiales =  mutableListOf<FavDetailItem>()

        items = materiales


        fetch_from_firebase_database("favoritos")

        // Read data from the table

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favoritos_admin, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDetail)
        searchView = view.findViewById(R.id.searchViewMaterial)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FavoritosAdapter(requireContext(), items, { item ->
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
                findNavController().navigate(R.id.action_material_to_youTubePlayerFragment)
            }
        }, { item ->
            // Handle toggle favorite
            this.removeFavorite(item)



        })
        recyclerView.adapter = adapter


        fetch_from_firebase_database("favoritos")
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




    private fun List<Map<String, Any?>>.toMyItemList(): List<FavDetailItem> {
        return this.map { map ->
            FavDetailItem(
                id = map["id"] as String,
                name = map["name"] as String,
                external_link = (map["external_link"] ?: "").toString(),
                tipo = map["tipo"] as String,
                temario_id = map["temario_id"] as String,
                like = true,
            )
        }
    }
    private fun List<Map<String, Any?>>.fetchFavoritos(): List<FavoritoDB> {
        return this.map { map ->
            FavoritoDB(
                id = map["id"] as String,
                uuid = map["uuid"] as String,
                material_id = map["material_id"] as String,
            )
        }
    }



    fun removeFavorite(item: FavDetailItem) {



        val db = Firebase.firestore


        db.collection("favoritos")
            .whereEqualTo("material_id", db.collection("material").document(item.id))
            .whereEqualTo("uuid", current_user?.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("favoritos").document(document.id).delete()
                }
                val new_items = items.filter { it.id != item.id }
                adapter.updateItems(new_items)
                items  = new_items
            }

    }

    private fun fetch_from_firebase_database(table: String) {


        val database = Firebase.firestore
        val results = mutableListOf<Map<String, Any?>>()
        val new_results = mutableListOf<FavDetailItem>()


        when (table) {
            "favoritos" -> {
                database.collection("favoritos")
                    .whereEqualTo("uuid",current_user?.uid)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {

                            val x =(document.data["material_id"] as DocumentReference).id

                            val data = hashMapOf(
                                "id" to document.id,
                                "uuid" to document.data["uuid"],
                                "material_id" to x as String,
                            )
                            results.add(data)
                        }
                        val new_list = results.fetchFavoritos()

                        new_list.forEach { item ->
                            database.collection("material").document(item.material_id.toString())
                                .get()
                                .addOnSuccessListener { doc ->
                                        val new_data = FavDetailItem(
                                            id = doc.id as String,
                                            name = doc.data?.get("name") as String,
                                            external_link = doc.data?.get("external_link") as String,
                                            tipo = doc.data?.get("tipo") as String,
                                            like = true,
                                            temario_id = (doc.data?.get("temario_id") as DocumentReference).id,
                                        )
                                        new_results.add(new_data)
                                        adapter.updateItems(new_results)
                                        items = new_results

                                }
                                .addOnFailureListener { exception ->
                                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                                }
                        }


                    }.addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
        }


    }



}