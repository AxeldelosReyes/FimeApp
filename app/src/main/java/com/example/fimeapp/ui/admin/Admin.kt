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
import com.example.fimeapp.databinding.ActivityAdminBinding
import com.example.fimeapp.R

class Admin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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
}