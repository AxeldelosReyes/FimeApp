package com.example.fimeapp.firebase_data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlanEstudio::class, Academia::class, Materias::class, Material::class, Temario::class], version = 26)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun academiaDao(): AcademiaDao
    abstract fun materiasDao(): MateriasDao
    abstract fun materialDao(): MaterialDao
    abstract fun temarioDao(): TemarioDao


    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Allow destructive migrations
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}
