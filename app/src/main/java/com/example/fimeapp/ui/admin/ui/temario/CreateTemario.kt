package com.example.fimeapp.ui.admin.ui.temario

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fimeapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class CreateTemario : Fragment() {

    companion object {
        fun newInstance() = CreateTemario()
    }

    private val viewModel: CreateTemarioViewModel by viewModels()

    private var plan_id : String = ""
    private var materia_id : String = ""
    private var academia_id : String = ""


    private var plan_name = ""
    private var materia_name= ""
    private var academia_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        plan_id = requireArguments().getString("plan_id").toString()
        materia_id = requireArguments().getString("materia_id").toString()
        academia_id = requireArguments().getString("academia_id").toString()


        plan_name = requireArguments().getString("plan_name").toString()
        materia_name = requireArguments().getString("materia_name").toString()
        academia_name = requireArguments().getString("academia_name").toString()

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_temario, container, false)



        view.setOnClickListener {

            createTemario()
        }


        return view
    }

    private fun createTemario() {

        val auth = Firebase.auth
        val database = Firebase.firestore


        val data = hashMapOf(
            "name" to "Tokyo",
            "country" to "Japan",
        )


        database.collection("temario")
            .add(data)
            .addOnSuccessListener { Log.d("DB_INSERT", "DocumentSnapshot successfully written!") }

            .addOnFailureListener { e -> Log.w("DB_INSERT", "Error writing document", e) }
    }



}
