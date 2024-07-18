package com.example.fimeapp.ui.admin.ui.temario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fimeapp.R

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

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_temario, container, false)
    }
}