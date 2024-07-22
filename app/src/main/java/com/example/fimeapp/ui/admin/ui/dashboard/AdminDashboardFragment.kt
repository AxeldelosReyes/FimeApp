package com.example.fimeapp.ui.admin.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fimeapp.LoginActivity
import com.example.fimeapp.databinding.FragmentDashboardAdminBinding
import com.example.fimeapp.databinding.FragmentDashboardBinding
import com.example.fimeapp.ui.dashboard.DashboardViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore


class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DashboardFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val user = com.google.firebase.ktx.Firebase.auth.currentUser
        user?.let {
            val email = it.email ?: "Correo no disponible"
            val userId = it.uid

            Log.d(TAG, "Usuario autenticado UID: $userId")

            binding.textCorreo.text = "Correo: $email"

            // Get additional user data from Firestore
            val db = com.google.firebase.ktx.Firebase.firestore
            val docRef = db.collection("usuarios").document(userId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: "Nombre no disponible"
                        val apellido = document.getString("apellido") ?: "Apellido no disponible"
                        val rol = document.getString("rol") ?: "Rol no disponible"

                        Log.d(TAG, "Nombre: $nombre, Apellido: $apellido, Rol: $rol")

                        binding.textNombre.text = "Nombre: $nombre"
                        binding.textApellidos.text = "Apellidos: $apellido"
                        binding.textRol.text = "Rol: $rol"
                    } else {
                        Log.w(TAG, "Documento de usuario no encontrado")
                        binding.textNombre.text = "Nombre: no disponible"
                        binding.textApellidos.text = "Apellidos: no disponible"
                        binding.textRol.text = "Rol: no disponible"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener el documento de usuario", exception)
                    binding.textNombre.text = "Nombre: Error al obtener"
                    binding.textApellidos.text = "Apellidos: Error al obtener"
                    binding.textRol.text = "Rol: Error al obtener"
                }
        }

        binding.cerrarBtn.setOnClickListener {
            com.google.firebase.ktx.Firebase.auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}