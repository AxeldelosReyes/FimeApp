package com.example.fimeapp

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
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
import com.example.fimeapp.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationBarView

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.visibility = View.GONE

        window.statusBarColor = getColor(R.color.black)

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_back, R.id.navigation_home, R.id.navigation_favoritos, R.id.navigation_dashboard
            ),
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Handle navigation item selection

        navView.menu.findItem(R.id.navigation_back).setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.navigation_back -> {
                    if (!navController.popBackStack()) {
//                        finish() // If there's no fragment to go back to, close the activity
                    }
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

            navView.menu.findItem(R.id.navigation_back).setEnabled(true)

            when (destination.id) {
                R.id.navigation_home -> {
                    navView.menu.findItem(R.id.navigation_back).setEnabled(false)
//                    navView.menu.findItem(R.id.navigation_home).isChecked = true
//                    navView.menu.findItem(R.id.navigation_back).isVisible = false
                }
                R.id.navigation_dashboard -> {
//                    navView.menu.findItem(R.id.navigation_dashboard).isChecked = true
//                    navView.menu.findItem(R.id.navigation_back).isVisible = false
                }
                R.id.navigation_favoritos -> {
//                    navView.menu.findItem(R.id.navigation_favoritos).isChecked = true
//                    navView.menu.findItem(R.id.navigation_back).isVisible = false
                }
//                R.id.navigation_back -> {navController.popBackStack()

                }
//                else -> {
//                    navView.menu.findItem(R.id.navigation_back).isVisible = true
//                    navView.menu.findItem(R.id.navigation_home).isChecked = true


//                }
//            }
//        }



    }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}