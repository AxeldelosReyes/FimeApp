package com.example.fimeapp.firebase_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_plan")
data class PlanEstudio(
    @PrimaryKey val id: String,
    val name: String?,
)

@Entity(tableName = "academias")
data class Academia(
    @PrimaryKey val id: String,
    val name: String?,
)

@Entity(tableName = "materias")
data class Materias(
    @PrimaryKey val id: String,
    val name: String?,
    val academia_id: String?,
)

@Entity(tableName = "material")
data class Material(
    @PrimaryKey val id: String,
    val name: String?,
    val temario_id: String?,
    val external_link: String?,
    val tipo: String?
)

@Entity(tableName = "temario")
data class Temario(
    @PrimaryKey val id: String,
    val name: String?,
    val materia_id: String?,
    val imagen_url: String?,
    val descripcion: String?
)



