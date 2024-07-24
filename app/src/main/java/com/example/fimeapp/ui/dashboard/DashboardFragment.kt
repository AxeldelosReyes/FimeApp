package com.example.fimeapp.ui.dashboard

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fimeapp.LoginActivity
import com.example.fimeapp.databinding.FragmentDashboardBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardFragment : Fragment() {

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

        val user = Firebase.auth.currentUser
        user?.let {
            val email = it.email ?: "Correo no disponible"
            val userId = it.uid

            Log.d(TAG, "Usuario autenticado UID: $userId")

            binding.textCorreo.text = formatTextWithHtml("Correo: ", email)

            // Get additional user data from Firestore
            val db = Firebase.firestore
            val docRef = db.collection("usuarios").document(userId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: "Nombre no disponible"
                        val apellido = document.getString("apellido") ?: "Apellido no disponible"
                        val rol = document.getString("rol") ?: "Rol no disponible"

                        Log.d(TAG, "Nombre: $nombre, Apellido: $apellido, Rol: $rol")

                        binding.textNombre.text = formatTextWithHtml("Nombre: ", nombre)
                        binding.textApellidos.text = formatTextWithHtml("Apellidos: ", apellido)
                        binding.textRol.text = formatTextWithHtml("Rol: ", rol)
                    } else {
                        Log.w(TAG, "Documento de usuario no encontrado")
                        binding.textNombre.text = formatTextWithHtml("Nombre: ", "no disponible")
                        binding.textApellidos.text = formatTextWithHtml("Apellidos: ", "no disponible")
                        binding.textRol.text = formatTextWithHtml("Rol: ", "no disponible")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener el documento de usuario", exception)
                    binding.textNombre.text = formatTextWithHtml("Nombre: ", "Error al obtener")
                    binding.textApellidos.text = formatTextWithHtml("Apellidos: ", "Error al obtener")
                    binding.textRol.text = formatTextWithHtml("Rol: ", "Error al obtener")
                }
        }

        binding.cerrarBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatTextWithHtml(label: String, value: String): Spanned {
        val htmlText = "<b>$label</b>$value"
        return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    }
    
}
