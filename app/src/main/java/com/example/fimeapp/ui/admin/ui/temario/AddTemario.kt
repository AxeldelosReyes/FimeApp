package com.example.fimeapp.ui.admin.ui.temario

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.fimeapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AddTemario : Fragment() {

    companion object {
        fun newInstance() = AddTemario()
    }

    private var plan_id : String = ""
    private var temario_id : String = ""
    private var materia_id : String = ""
    private var academia_id : String = ""


    private var plan_name = ""
    private var materia_name= ""
    private var academia_name = ""

    private var nombre : TextInputEditText? = null
    private var descripcion : TextInputEditText? = null
    private var urlInput : EditText? = null

    private val viewModel: AddTemarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plan_id = requireArguments().getString("plan_id").toString()
        temario_id = requireArguments().getString("temario_id").toString()
        materia_id = requireArguments().getString("materia_id").toString()
        academia_id = requireArguments().getString("academia_id").toString()


        plan_name = requireArguments().getString("plan_name").toString()
        materia_name = requireArguments().getString("materia_name").toString()
        academia_name = requireArguments().getString("academia_name").toString()


        fetch_from_firebase_database("temario")


        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_temario, container, false)


        nombre = view?.findViewById(R.id.addTittle)
        descripcion =  view?.findViewById(R.id.addText)
        urlInput =  view?.findViewById(R.id.urlInput)

        fetch_from_firebase_database("temario")


        view.findViewById<AppCompatImageView>(R.id.iconSave).setOnClickListener {

            if (urlInput?.text == null
                || nombre?.text == null
                || descripcion?.text == null){
                Log.e("ERROR", "ERROR")
            }else{
                savetoFirebase(hashMapOf(
                    "imagen_url" to urlInput?.text.toString(),
                    "name" to  nombre?.text.toString(),
                    "descripcion" to  descripcion?.text.toString(),
                    "materia" to Firebase.firestore.collection("materias").document(materia_id),
                ))
            }





        }

        view.findViewById<ImageView>(R.id.iconDelete).setOnClickListener {

            val db = Firebase.firestore
            val current_user = FirebaseAuth.getInstance().currentUser

            db.collection("temario").document(temario_id).delete()
                .addOnSuccessListener { result ->
                    requireActivity().onBackPressedDispatcher.onBackPressed()

                }
        }


        return view
    }

    private fun fetch_from_firebase_database(table: String) {

        val database = Firebase.firestore

        when (table) {
            "temario" -> {
                database.collection("temario")
                    .document(temario_id)
                    .get()
                    .addOnSuccessListener { result ->
                        descripcion?.setText(result.get("descripcion").toString())
                        nombre?.setText(result.get("name").toString())
                        urlInput?.setText(result.get("imagen_url").toString())

                    }.addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
        }


    }

    private fun savetoFirebase(vals : HashMap<String, Any>) {

        val db = Firebase.firestore

        db.collection("temario").document(temario_id).set(vals).addOnSuccessListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }




}