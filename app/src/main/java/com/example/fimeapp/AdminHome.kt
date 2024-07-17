package com.example.fimeapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fimeapp.databinding.ActivityAdminHomeBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar

class AdminHome : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()

        }




        val navView: BottomNavigationView = binding.navView
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.visibility = View.GONE

        window.statusBarColor = getColor(R.color.black)

        val navController = findNavController(R.id.nav_host_fragment_activity_home_admin)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_favoritos
            ),
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Handle navigation item selection


        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.adminHomeFragment -> {
                    navView.menu.findItem(R.id.adminHomeFragment).isChecked = true
                }
                R.id.navigation_dashboard -> {
                    navView.menu.findItem(R.id.navigation_dashboard).isChecked = true
                }
                R.id.navigation_favoritos -> {
                    navView.menu.findItem(R.id.navigation_favoritos).isChecked = true
                }
                else -> {
                    navView.menu.findItem(R.id.adminHomeFragment).isChecked = true
                }
            }
        }



    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.adminHomeFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}