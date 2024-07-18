package com.example.fimeapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fimeapp.databinding.ActivityLoginBinding
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.fimeapp.ui.admin.Admin
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.firestore

        binding.continueBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showErrorDialog("Por favor, ingresa el correo y la contraseña.")
            } else {
                showProgressBar()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        hideProgressBar()
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser

                            if (user != null) {
                                checkUserRole(user.uid)
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            showErrorDialog("Usuario capturado no existe. Favor de revisar en su dependencia.")
                        }
                    }
            }
        }

        binding.skip.setOnClickListener {
            val intent = Intent(this, Home::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Start the Activity
            startActivity(intent)
        }


        binding.skipadmin.setOnClickListener {
            val intent = Intent(this, Admin::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Start the Activity
            startActivity(intent)
        }

        // Implementación del alternador de visibilidad de contraseña
        binding.passwordLayout.setEndIconOnClickListener {
            val inputType = binding.password.inputType
            if (inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.passwordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_visibility)
            } else {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_visibility_off)
            }
            // Mueve el cursor al final del texto
            binding.password.setSelection(binding.password.text?.length ?: 0)
        }
    }


    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)

        val titleView = TextView(this).apply {
            text = "Acceso Denegado"
            setTextAppearance(R.style.CustomAlertDialogTitle)
            setPadding(16, 16, 16, 16)
            gravity = Gravity.CENTER
        }

        builder.setCustomTitle(titleView)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.continueBtn.isEnabled = false
        binding.email.isEnabled = false
        binding.password.isEnabled = false
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.continueBtn.isEnabled = true
        binding.email.isEnabled = true
        binding.password.isEnabled = true
    }

    private fun checkUserRole(userId: String) {


        val docRef = database.collection("usuarios").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val role = document.getString("rol")
                    if (role == "admin") {
                        val intent = Intent(this, Admin::class.java)
                        startActivity(intent)
                    }
                    else if (role == "user") {
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                    }
                } else {
                    Log.d("UserRole", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("UserRole", "get failed with ", exception)
            }
    }

}
