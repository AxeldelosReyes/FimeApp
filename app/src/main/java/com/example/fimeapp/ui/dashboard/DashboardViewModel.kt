package com.example.fimeapp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Nombre: Saul Isaias\nApellidos: Leija Soriano\nCorreo:saul.leijaso@uanl.edu.mx"
    }
    val text: LiveData<String> = _text
}