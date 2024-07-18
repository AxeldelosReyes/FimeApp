package com.example.fimeapp.ui.admin.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fimeapp.LoginActivity
import com.example.fimeapp.databinding.FragmentDashboardAdminBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentDashboardAdminBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cerrarBtn.setOnClickListener{

            Firebase.auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)

        }

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}