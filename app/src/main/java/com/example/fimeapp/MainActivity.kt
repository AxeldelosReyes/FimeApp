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

import com.example.fimeapp.db_manager.DBHelper


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        window.statusBarColor = getColor(R.color.black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val test = findViewById<TextView>(R.id.testTextView)
        val loginBtn = findViewById<Button>(R.id.btnLogin)

        loginBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Start the Activity
            startActivity(intent)

        }

        test.setOnClickListener {
            test.text = "Clicked"

        }
        Log.e("DB", "test")
        setup()

    }

    private fun setup() {
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        // Safely query the database
        var cursor: Cursor? = null
        Log.d("DB", "TEST")
        try {
            cursor = db.rawQuery("SELECT * FROM study_plan", null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    Log.d("DB", "id: $id, Email: $name")
                }
            }
        } catch (e: SQLException) {
            Log.e("DB", "Query failed", e)
        } finally {
            cursor?.close()
        }

    }


}