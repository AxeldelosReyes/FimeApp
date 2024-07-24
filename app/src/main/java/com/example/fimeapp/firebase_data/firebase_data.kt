package com.example.fimeapp.firebase_data

import com.example.fimeapp.ui.admin.ui.favoritos.FavDetailItem
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot


data class PlanEstudioFirebase(
    val nombre: String?,
)

data class AcademiaFirebase(
    val nombre: String?,
)

data class MateriasFirebase(
     val id: String?,
    val nombre: String?,
    val academia: String?,
)

data class MaterialFirebase(
     val id: String,
    val name: String?,
    val temario_id: String?,
    val external_link: String?,
    val tipo: String?
)

data class TemarioFirebase(
     val id: String,
    val name: String?,
    val materia_id: String?,
    val imagen_url: String?,
    val descripcion: String?
)


fun QuerySnapshot.toMateriasList(): List<Materias> {
    val allList = mutableListOf<Materias>()
    for (document in this.documents) {
        allList.add(Materias(document.id as String, document.data?.get("nombre") as String, (document.data?.get("academia") as DocumentReference).id))
    }
    return allList
}


fun QuerySnapshot.toAcademiaList(): List<Academia> {
    val allList = mutableListOf<Academia>()
    for (document in this.documents) {
        document.data?.let { doc ->
            allList.add(Academia(document.id as String, doc["nombre"] as String))
        }
    }
    return allList
}


fun QuerySnapshot.toplanList(): List<PlanEstudio> {
    val allList = mutableListOf<PlanEstudio>()
    for (document in this.documents) {
        document.data?.let { doc ->
            allList.add(PlanEstudio(document.id as String, doc["name"] as String))
        }
    }
    return allList
}


