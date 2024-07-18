package com.example.fimeapp.ui.admin.ui.material

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fimeapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
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

    private var plan_id : String = ""
    private var temario_id : String = ""
    private var materia_id : String = ""
    private var academia_id : String = ""


    private var plan_name = ""
    private var materia_name= ""
    private var academia_name = ""
    private var tipo = ""


    private lateinit var final_pdf_url: EditText
    private lateinit var url_video: EditText
    private lateinit var nombre: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel

        plan_id = requireArguments().getString("plan_id").toString()
        temario_id = requireArguments().getString("temario_id").toString()
        materia_id = requireArguments().getString("materia_id").toString()
        academia_id = requireArguments().getString("academia_id").toString()


        plan_name = requireArguments().getString("plan_name").toString()
        materia_name = requireArguments().getString("materia_name").toString()
        academia_name = requireArguments().getString("academia_name").toString()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_material, container, false)

        nombre = view.findViewById(R.id.fileName)
        url_video  = view.findViewById(R.id.videoUrl)
        final_pdf_url = view.findViewById(R.id.urlInput)


        view.findViewById<AppCompatImageView>(R.id.iconSave).setOnClickListener {

            tipo = if (!url_video.text.isNullOrEmpty()) "video" else "pdf"



            if (nombre?.text == null
                || final_pdf_url == null
                || tipo == null
            ) {
                Log.e("ERROR", "ERROR")
            } else {
                val url = if (tipo == "video") {url_video.text.toString()} else final_pdf_url.text.toString()
                savetoFirebase(hashMapOf(
                    "name" to nombre.text.toString(),
                    "external_link" to final_pdf_url.text.toString(),
                    "tipo" to tipo,
                    "temario_id" to Firebase.firestore.collection("temario").document(temario_id)

                ))
            }

        }


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
                final_pdf_url.setText(downloadUri.toString())
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

    private fun savetoFirebase(vals : HashMap<String, Any>) {

        val db = Firebase.firestore

        db.collection("material").add(vals).addOnSuccessListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        .addOnFailureListener { e ->
            Log.w("TAG", "Error adding document", e)
        }
    }



}
