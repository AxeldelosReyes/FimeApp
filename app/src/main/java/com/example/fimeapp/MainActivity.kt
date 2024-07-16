package com.example.fimeapp

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.SQLException
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fimeapp.databinding.ActivityLoginBinding
import com.example.fimeapp.databinding.ActivityMainBinding

import com.example.fimeapp.db_manager.DBHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val test = findViewById<TextView>(R.id.testTextView)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        auth = Firebase.auth
        binding.cerrarBtn.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        loginBtn.setOnClickListener {
//            val intent = Intent(this, Home::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            // Start the Activity
//            startActivity(intent)
//
//        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        test.setOnClickListener {
            test.text = "Clicked"

        }

    }


}