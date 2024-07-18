package com.example.fimeapp.ui.favoritos

import android.content.ContentValues
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
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
import com.example.fimeapp.ui.material.DetailAdapter
import com.example.fimeapp.ui.material.DetailItem
import com.example.fimeapp.ui.material.MaterialViewModel
import com.example.fimeapp.ui.material.material
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo


data class FavoritoDB(
    val id: Int,
    val uuid: String,
    val material_id: Int,
    val academia_id: Int,
    val materia_id: Int,
)



class favoritos : Fragment() {

    companion object {
        fun newInstance() = favoritos()
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


        val all_favoritos = databaseHelper.read("favoritos", arrayOf("id","uuid","academia_id","materia_id","material_id"),"uuid= ?", arrayOf(current_user?.uid ?: "prueba")).fetchFavoritos()

        val materiales =  mutableListOf<FavDetailItem>()
        all_favoritos.forEach { item ->
            val tema = databaseHelper.read("material", arrayOf("id",
                "tipo","name","external_link","temario_id","asset","uri"),"id= ?", arrayOf(item.material_id.toString())).toMyItemList()
            materiales += tema
        }
        items = materiales

        // Read data from the table

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favoritos, container, false)
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
                } else if (item.uri.isNotEmpty()) {
                    startActivity(
                        PdfViewerActivity.launchPdfFromPath(
                            context = this.context,
                            path = item.uri,
                            pdfTitle = item.name,
                            saveTo = saveTo.ASK_EVERYTIME,
                            fromAssets = false
                        )
                    )
                } else {
                    startActivity(
                        PdfViewerActivity.launchPdfFromPath(
                            context = this.context,
                            path = item.asset,
                            pdfTitle = item.name,
                            saveTo = saveTo.ASK_EVERYTIME,
                            fromAssets = true
                        )
                    )
                }
            } else if (item.tipo == "video") {
                findNavController().navigate(R.id.action_material_to_youTubePlayerFragment)
            }
        }, { item ->
            // Handle toggle favorite
            this.removeFavorite(item)
            val new_items = items.filter { it.id != item.id }
            adapter.updateItems(new_items)



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




    private fun List<Map<String, Any?>>.toMyItemList(): List<FavDetailItem> {
        return this.map { map ->
            FavDetailItem(
                id = map["id"] as Int,
                name = map["name"] as String,
                external_link = (map["external_link"] ?: "").toString(),
                tipo = map["tipo"] as String,
                uri = (map["uri"] ?: "").toString(),
                asset = (map["asset"] ?: "").toString(),
                temario_id = map["temario_id"] as Int,
                like = true,
            )
        }
    }
    private fun List<Map<String, Any?>>.fetchFavoritos(): List<FavoritoDB> {
        return this.map { map ->
            FavoritoDB(
                id = map["id"] as Int,
                uuid = map["uuid"] as String,
                materia_id = map["materia_id"] as Int,
                academia_id = map["academia_id"] as Int,
                material_id = map["material_id"] as Int,
            )
        }
    }



    fun removeFavorite(item: FavDetailItem) {
        val db = databaseHelper.writableDatabase

        val rowsDeleted = db.delete("favoritos", "material_id=? and uuid=?", arrayOf(item.id.toString(), current_user?.uid  ?: "prueba"))
        if (rowsDeleted == 0) {
            Log.e("DB_DELETE", "Failed to delete item from favoritos")
        } else {

            Log.i("DB_DELETE", "Item deleted from favoritos")
        }

    }


}