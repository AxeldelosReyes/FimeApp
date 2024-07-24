package com.example.fimeapp.ui.admin

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.fimeapp.databinding.ActivityAdminBinding
import com.example.fimeapp.R
import com.example.fimeapp.firebase_data.Academia
import com.example.fimeapp.firebase_data.AcademiaDao
import com.example.fimeapp.firebase_data.AppDatabase
import com.example.fimeapp.firebase_data.MaterialDao
import com.example.fimeapp.firebase_data.MateriasDao
import com.example.fimeapp.firebase_data.PlanDao
import com.example.fimeapp.firebase_data.TemarioDao
import com.example.fimeapp.firebase_data.FirebaseHelper
import com.example.fimeapp.firebase_data.Materias
import com.example.fimeapp.firebase_data.PlanEstudio
import com.example.fimeapp.firebase_data.toAcademiaList
import com.example.fimeapp.firebase_data.toMateriasList
import com.example.fimeapp.firebase_data.toplanList
import com.google.firebase.database.FirebaseDatabase


class Admin : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var planDao: PlanDao
    private lateinit var academiaDao: AcademiaDao
    private lateinit var materiasDao: MateriasDao
    private lateinit var materialDao: MaterialDao
    private lateinit var temarioDao: TemarioDao
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDB()

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView22
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.visibility = View.GONE
        window.statusBarColor = getColor(R.color.black)
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val navController = findNavController(R.id.nav_host_fragment_activity_admin)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
               R.id.navigation_favoritos_admin, R.id.navigation_home_admin, R.id.navigation_dashboard_admin
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home_admin -> {
                    navView.menu.findItem(R.id.navigation_home_admin).isChecked = true
                }
                R.id.navigation_dashboard_admin -> {
                    navView.menu.findItem(R.id.navigation_dashboard_admin).isChecked = true
                }
                R.id.navigation_favoritos_admin -> {
                    navView.menu.findItem(R.id.navigation_favoritos_admin).isChecked = true
                }
                else -> {
                    navView.menu.findItem(R.id.navigation_home_admin).isChecked = true
                }
            }
        }


    }

    private fun setupDB(){

        database = AppDatabase.getDatabase(this)

        planDao = database.planDao()
        academiaDao = database.academiaDao()
        materiasDao = database.materiasDao()

        firebaseHelper = FirebaseHelper()


        firebaseHelper.fetchDataFromFirestore(
            "study_plan",
            { snapshot -> snapshot.toplanList() },
            { items -> planDao.insertAll(items) }
        )

        firebaseHelper.fetchDataFromFirestore(
            "academias",
            { snapshot -> snapshot.toAcademiaList() },
            { items -> academiaDao.insertAll(items) }
        )

        firebaseHelper.fetchDataFromFirestore(
            "materias",
            { snapshot -> snapshot.toMateriasList() },
            { items -> materiasDao.insertAll(items) }
        )

        materialDao = database.materialDao()
        temarioDao = database.temarioDao()
    }
}