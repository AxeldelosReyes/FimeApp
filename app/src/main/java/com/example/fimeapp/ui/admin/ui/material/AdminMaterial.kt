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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fimeapp.R
import com.example.fimeapp.db_manager.DBHelper
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth


class AdminMaterial : Fragment() {

    private lateinit var databaseHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetailAdapter
    private lateinit var items: List<DetailItem>
    private lateinit var searchView: SearchView

    private var current_user: FirebaseUser? = null
    private var temario_id = 0
    private var plan_id = 0
    private var materia_id = 0
    private var academia_id = 0

    private var plan_name = ""
    private var materia_name= ""
    private var academia_name = ""


    companion object {
        fun newInstance() = AdminMaterial()
    }

    private val viewModel: MaterialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DBHelper(requireContext())

        current_user = Firebase.auth.currentUser

        temario_id = requireArguments().getInt("temario")
        plan_id = requireArguments().getInt("plan")
        materia_id = requireArguments().getInt("materia")
        academia_id = requireArguments().getInt("academia")

        plan_name = databaseHelper.readName("study_plan", "name","id", plan_id.toString()) ?: ""
        materia_name = databaseHelper.readName("materias", "name","id", materia_id.toString()) ?: ""
        academia_name = databaseHelper.readName("academias", "name","id", academia_id.toString()) ?: ""


        // Read data from the table
        items = databaseHelper.read("material", arrayOf("id",
            "tipo","name","external_link","temario_id","asset","uri"),"temario_id= ?", arrayOf(temario_id.toString()), orderBy ="name").toMyItemList()

        items.forEach { item ->
            item.like = this.isFavorite(item.id)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_material_admin, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDetail)
        searchView = view.findViewById(R.id.searchViewMaterial)

        val iconAdd = view.findViewById<ImageView>(R.id.iconAdd)

        iconAdd.setOnClickListener{
            val bundle = Bundle().apply {
                putInt("academia", academia_id)
            }
            findNavController().navigate(R.id.action_adminMaterial_to_createMaterial, bundle)


        }

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
                id = map["id"] as Int,
                name = map["name"] as String,
                external_link = (map["external_link"] ?: "").toString(),
                tipo = map["tipo"] as String,
                uri = (map["uri"] ?: "").toString(),
                asset = (map["asset"] ?: "").toString(),
                temario_id = map["temario_id"] as Int,
                like = false,
            )
        }
    }


    private fun isFavorite(id: Int): Boolean {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM favoritos WHERE material_id=? and uuid=?", arrayOf(id.toString(), current_user?.uid ?: "prueba"))
        val isFavorite = cursor.count > 0
        cursor.close()
        return isFavorite
    }

    fun toggleFavorite(item: DetailItem) {
        val db = databaseHelper.writableDatabase
        if (item.like) {
            // Insert into favoritos
            val contentValues = ContentValues().apply {
                put("material_id", item.id)
                put("materia_id", materia_id)
                put("academia_id", academia_id)
                put("uuid", current_user?.uid ?: "prueba")
            }
            val newRowId = db.insert("favoritos", null, contentValues)
            if (newRowId == -1L) {
                Log.e("DB_INSERT", "Failed to insert item into favoritos")
            } else {
                Log.i("DB_INSERT", "Item inserted into favoritos with row ID: $newRowId")
            }

        } else {
            val rowsDeleted = db.delete("favoritos", "material_id=? and uuid=?", arrayOf(item.id.toString(), current_user?.uid  ?: "prueba"))
            if (rowsDeleted == 0) {
                Log.e("DB_DELETE", "Failed to delete item from favoritos")
            } else {
                Log.i("DB_DELETE", "Item deleted from favoritos")
            }
        }
    }


}