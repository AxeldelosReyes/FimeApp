package com.example.fimeapp.ui.admin.ui.temario

import android.annotation.SuppressLint
import android.app.Activity
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import com.example.fimeapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore


class CreateTemario : Fragment() {

    companion object {
        fun newInstance() = CreateTemario()
    }

    private val viewModel: CreateTemarioViewModel by viewModels()
    private var imageUri: Uri? = null
    private var downloadUri: String? = null
    private lateinit var urlInput: EditText

    private var plan_id : String = ""
    private var materia_id : String = ""
    private var academia_id : String = ""


    private var plan_name = ""
    private var materia_name= ""
    private var academia_name = ""

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        plan_id = requireArguments().getString("plan_id").toString()
        materia_id = requireArguments().getString("materia_id").toString()
        academia_id = requireArguments().getString("academia_id").toString()


        plan_name = requireArguments().getString("plan_name").toString()
        materia_name = requireArguments().getString("materia_name").toString()
        academia_name = requireArguments().getString("academia_name").toString()

        // TODO: Use the ViewModel
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_temario, container, false)

        // Configurar el botón para seleccionar una imagen
        view.findViewById<Button>(R.id.select_image_button).setOnClickListener {
            selectImageFromGallery()
        }




        view.findViewById<AppCompatImageView>(R.id.iconSave).setOnClickListener {

            val nombre =  view.findViewById<TextInputEditText>(R.id.addTittle).text.toString()
            val descripcion =  view.findViewById<TextInputEditText>(R.id.addText).text.toString()



            savetoFirebase(hashMapOf(
                "imagen_url" to urlInput.text.toString(),
                "materia" to Firebase.firestore.collection("materias").document(materia_id),
                "name" to  nombre,
                "descripcion" to descripcion

            ))
        }
        urlInput = view.findViewById(R.id.urlInput)

        return view
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        selectImageLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${uri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result.toString()

//                this.urlInput.text = downloadUri.toString()
                saveImageUrlToDatabase(downloadUri.toString())
                urlInput.setText(downloadUri.toString())
            } else {
                // Handle failures
            }
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("images")
        myRef.push().setValue(imageUrl)
    }


    private fun savetoFirebase(vals : HashMap<String, Any>) {

        val db = Firebase.firestore

        db.collection("temario").add(vals).addOnSuccessListener {
           requireActivity().onBackPressedDispatcher.onBackPressed()
        }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
        }
    }
}
