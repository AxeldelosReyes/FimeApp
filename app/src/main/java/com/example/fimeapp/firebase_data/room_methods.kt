package com.example.fimeapp.firebase_data



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy


@Dao
interface PlanDao {
    @Query("SELECT * FROM study_plan")
    suspend fun getAll(): List<PlanEstudio>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: PlanEstudio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plan: List<PlanEstudio>)
}


@Dao
interface AcademiaDao {
    @Query("SELECT * FROM academias")
    suspend fun getAll(): List<Academia>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(academia: Academia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(academias: List<Academia>)
}


@Dao
interface MateriasDao {
    @Query("SELECT * FROM materias")
    suspend fun getAll(): List<Materias>

    @Query("SELECT * FROM materias WHERE academia_id = :academiaId")
    suspend fun getByAcademiaId(academiaId: String): List<Materias>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(materia: Materias)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materias: List<Materias>)
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM material")
    suspend fun getAll(): List<Material>

    @Query("SELECT * FROM material WHERE temario_id = :temarioId")
    suspend fun getByTemarioId(temarioId: String): List<Material>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: Material)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materials: List<Material>)
}


@Dao
interface TemarioDao {
    @Query("SELECT * FROM temario")
    suspend fun getAll(): List<Temario>

    @Query("SELECT * FROM temario WHERE materia_id = :materiaId")
    suspend fun getByMateriaId(materiaId: String): List<Temario>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(temario: Temario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(temarios: List<Temario>)
}
