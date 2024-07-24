package com.example.fimeapp.ui.admin.ui.home

import android.content.Intent
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
import com.example.fimeapp.Home
import com.example.fimeapp.R
import com.example.fimeapp.databinding.FragmentHomeAdminBinding
import com.example.fimeapp.db_manager.DBHelper
import com.example.fimeapp.firebase_data.AppDatabase
import com.example.fimeapp.firebase_data.TemarioDao
import com.example.fimeapp.firebase_data.AcademiaDao
import com.example.fimeapp.ui.admin.Admin
import com.example.fimeapp.ui.admin.ui.home.CustomSpinnerAdapter
import com.example.fimeapp.ui.admin.ui.home.HomeViewModel
import com.example.fimeapp.ui.admin.ui.home.SpinnerItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminHomeFragment : Fragment() {

    private lateinit var academiaDao: AcademiaDao
    private lateinit var database: AppDatabase



    private var _binding: FragmentHomeAdminBinding? = null
    private lateinit var databaseHelper: DBHelper
    private val binding get() = _binding!!
    private val DEFAULT_MATERIA = listOf( SpinnerItem(id="0", name="Materia"))
    private val DEFAULT_ACADEMIA = listOf( SpinnerItem(id="0", name="Academia"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root



        setupLogin()
        setupSpinners()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getDatabase(requireContext())
        academiaDao = database.academiaDao()

        // Example: Fetch all plans and print them
        CoroutineScope(Dispatchers.IO).launch {
            println(academiaDao.getAll())
        }
    }

    private fun setupLogin() {
        binding.btnLogin.setOnClickListener {
            val selectedPlan =  binding.spinnerPlan.selectedItem as SpinnerItem
            val selectedAcademia =  binding.spinnerAcademia.selectedItem as SpinnerItem
            val selectedMateria =  binding.spinnerMateria.selectedItem as SpinnerItem

            if (selectedPlan.id == "0" || selectedAcademia.id == "0" || selectedMateria.id == "0"){
                println("Error")
            }else{
                val bundle = Bundle().apply {
                    putString("plan_id", selectedPlan.id)
                    putString("academia_id", selectedAcademia.id)
                    putString("materia_id", selectedMateria.id)
                    putString("plan_name", selectedPlan.name)
                    putString("academia_name", selectedAcademia.name)
                    putString("materia_name", selectedMateria.name)
                }
                findNavController().navigate(R.id.action_navigation_home_admin_to_adminTemario, bundle)

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

        val plans = listOf( SpinnerItem(id="0", name="Plan de estudios"))
        fetch_from_firebase_database("study_plan")

        val planAdapter = CustomSpinnerAdapter(requireContext(),plans, R.drawable.ic_calendar_btn_black_24dp)
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val academias = DEFAULT_ACADEMIA
        val academiaAdapter = CustomSpinnerAdapter(requireContext(),academias, R.drawable.ic_building_btn_black_24dp)
        academiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fetch_from_firebase_database("academias")



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
                    fetch_from_firebase_database("materias")
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
                    fetch_from_firebase_database("materias")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    private fun resetSpinner(spinner: Spinner) {
        spinner.setSelection(0)
    }

    private fun fetch_from_firebase_database(table: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val database = Firebase.firestore
        val results = mutableListOf<Map<String, Any?>>()

        when (table) {
            "study_plan" -> {
                database.collection("study_plan")
                    .orderBy("name")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val data = hashMapOf(
                                "id" to document.id,
                                "name" to document.data["name"] as String,
                            )
                            results.add(data)
                        }
                        val adaptor = binding.spinnerPlan.adapter as CustomSpinnerAdapter
                        adaptor.updateItems(
                            listOf( SpinnerItem(id="0", name="Plan de estudios")) +
                            results.map {
                            SpinnerItem(
                                it["id"] as String,
                                it["name"] as String
                            )
                        })

                    }.addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
            "academias" -> {
                database.collection("academias")
                    .orderBy("nombre")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val data = hashMapOf(
                                "id" to document.id as String,
                                "name" to document.data["nombre"] as String,
                            )

                            results.add(data)
                        }
                        val adaptor = binding.spinnerAcademia.adapter as CustomSpinnerAdapter
                        adaptor.updateItems(DEFAULT_ACADEMIA +
                                results.map { SpinnerItem(it["id"] as String, it["name"] as String)
                                })


                    }.addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
            "materias" -> {
                val selectedAcademia =  binding.spinnerAcademia.selectedItem as SpinnerItem
                database.collection("materias")
                    .whereEqualTo("academia", database.collection("academias").document(selectedAcademia.id))
                    .orderBy("nombre")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val data = hashMapOf(
                                "id" to document.id,
                                "name" to document.data["nombre"] as String,
                            )
                            results.add(data)
                        }
                        val materiaAdaptor = binding.spinnerMateria.adapter as CustomSpinnerAdapter
                        materiaAdaptor.updateItems( DEFAULT_MATERIA + results.map { SpinnerItem(it["id"] as String, it["name"] as String) })
                    }.addOnFailureListener { exception ->
                        Log.w("FIREBASE", "Error getting documents: ", exception)
                    }
            }
        }


    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}