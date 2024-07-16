package com.example.fimeapp.ui.home

import android.database.Cursor
import android.database.SQLException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fimeapp.R
import com.example.fimeapp.databinding.FragmentHomeBinding
import com.example.fimeapp.db_manager.DBHelper

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var databaseHelper: DBHelper
    private val binding get() = _binding!!
    private val DEFAULT_MATERIA = listOf( SpinnerItem(id=0, name="Materia"))
    private val DEFAULT_ACADEMIA = listOf( SpinnerItem(id=0, name="Academia"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        if (savedInstanceState == null){
            setupLogin()
            setupSpinners()
        }


        return root
    }

    private fun setupLogin() {
        binding.btnLogin.setOnClickListener {
            val selectedPlan =  binding.spinnerPlan.selectedItem as SpinnerItem
            val selectedAcademia =  binding.spinnerAcademia.selectedItem as SpinnerItem
            val selectedMateria =  binding.spinnerMateria.selectedItem as SpinnerItem

            if (selectedPlan.id == 0 || selectedAcademia.id == 0 || selectedMateria.id == 0){
                println("Error")
            }else{
                val bundle = Bundle().apply {
                    putInt("plan", selectedPlan.id)
                    putInt("academia", selectedAcademia.id)
                    putInt("materia", selectedMateria.id)
                }
                findNavController().navigate(R.id.action_navigation_home_to_temario, bundle)

            }

        }
    }

    private fun setupSpinners() {
        databaseHelper = DBHelper(requireContext())
        // Initialize spinners with default values
        setSpinners()
        setSpinnerListeners()
    }

    private fun setSpinners() {
        val plans = listOf( SpinnerItem(id=0, name="Plan de estudios")) + databaseHelper.read("study_plan", arrayOf("id", "name")).map { SpinnerItem(it["id"] as Int, it["name"] as String) }
        val planAdapter = CustomSpinnerAdapter(requireContext(),plans, R.drawable.ic_calendar_btn_black_24dp)
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val academias = DEFAULT_ACADEMIA + databaseHelper.read("academias", arrayOf("id", "name")).map { SpinnerItem(it["id"] as Int, it["name"] as String) }
        val academiaAdapter = CustomSpinnerAdapter(requireContext(),academias, R.drawable.ic_building_btn_black_24dp)
        academiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        val materias = DEFAULT_MATERIA
        val materiaAdapter = CustomSpinnerAdapter(requireContext(),materias, R.drawable.ic_book_btn_black_24dp)
        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        binding.spinnerPlan.adapter = planAdapter
        binding.spinnerAcademia.adapter = academiaAdapter
        binding.spinnerMateria.adapter = materiaAdapter

    }


    private fun setSpinnerListeners() {
        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) {}
                else if (position == 0) {
                    resetSpinner(binding.spinnerMateria)
                } else {
                    update_materia_items()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        binding.spinnerAcademia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) {}
                else if (position == 0) {
                    resetSpinner(binding.spinnerMateria)
                } else {
                    resetSpinner(binding.spinnerMateria)
                    update_materia_items()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    private fun update_materia_items() {
        val selectedPlan =  binding.spinnerPlan.selectedItem
        val selectedAcademia =  binding.spinnerAcademia.selectedItem as SpinnerItem
        val db = databaseHelper.readableDatabase

        val materiasList = mutableListOf<SpinnerItem>()

        materiasList += DEFAULT_MATERIA
        // Safely query the database
        var cursor: Cursor? = null
        Log.d("DB", "TEST")
        try {
            cursor = db.rawQuery("select m.id,m.name from materias_academias_rel mar " +
                    "left join academias a on mar.academia_id = a.id " +
                    "left join materias m on mar.materia_id = m.id " +
                    "where a.id = ? ", arrayOf(selectedAcademia.id.toString()))
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val spinnerItem = SpinnerItem(id.toInt(), name)
                    materiasList.add(spinnerItem)
                }
            }
        } catch (e: SQLException) {
            Log.e("DB", "Query failed", e)
        } finally {
            cursor?.close()
        }
        val materiaAdaptor = binding.spinnerMateria.adapter as CustomSpinnerAdapter
        materiaAdaptor.updateItems(materiasList)

    }

    private fun resetSpinner(spinner: Spinner) {
        spinner.setSelection(0)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}