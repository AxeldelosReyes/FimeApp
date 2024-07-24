package com.example.fimeapp.firebase_data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseHelper(private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {

    fun <T> fetchDataFromFirestore(
        firestoreCollection: String,
        convert: (QuerySnapshot) -> List<T>,
        cacheData: suspend (List<T>) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionRef = firestore.collection(firestoreCollection)

        collectionRef.get().addOnSuccessListener { snapshot ->
            val items = convert(snapshot)
            cacheDataLocally(items, cacheData)
        }.addOnFailureListener { exception ->
            // Handle possible errors.
        }
    }

    private fun <T> cacheDataLocally(items: List<T>, cacheData: suspend (List<T>) -> Unit) {
        scope.launch {
            cacheData(items)
        }
    }
}
