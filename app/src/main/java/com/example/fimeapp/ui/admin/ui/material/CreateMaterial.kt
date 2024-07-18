package com.example.fimeapp.ui.admin.ui.material

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fimeapp.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class CreateMaterial : Fragment() {

    companion object {
        fun newInstance() = CreateMaterial()
    }

    private val viewModel: CreateMaterialViewModel by viewModels()
    private var pdfUri: Uri? = null

    private val selectPdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pdfUri = result.data?.data
            pdfUri?.let { uri ->
                uploadPdfToFirebase(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_material, container, false)

        // Configurar el botón para seleccionar un PDF
        view.findViewById<Button>(R.id.select_pdf_button).setOnClickListener {
            selectPdfFromStorage()
        }

        return view
    }

    private fun selectPdfFromStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        selectPdfLauncher.launch(intent)
    }

    private fun uploadPdfToFirebase(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val pdfRef = storageRef.child("pdfs/${uri.lastPathSegment}")
        val uploadTask = pdfRef.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            pdfRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                savePdfUrlToDatabase(downloadUri.toString())
            } else {
                // Handle failures
            }
        }
    }

    private fun savePdfUrlToDatabase(pdfUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("pdfs")
        myRef.push().setValue(pdfUrl)
    }
}
