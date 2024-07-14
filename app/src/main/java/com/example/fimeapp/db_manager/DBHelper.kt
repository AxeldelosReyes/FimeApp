package com.example.fimeapp.db_manager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 5
    }

    private val dbPath: String = context.getDatabasePath(DATABASE_NAME).path

    init {
        try {
            createDatabase()
        } catch (e: IOException) {
            throw RuntimeException("Error creating database", e)
        }
    }

    @Throws(IOException::class)
    fun createDatabase() {
        if (!checkDatabase()) {
            this.readableDatabase
            this.close()
            copyDatabase()
        }
    }

    private fun checkDatabase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: Exception) {
            // Database doesn't exist yet
        }
        checkDB?.close()
        return checkDB != null
    }

    @Throws(IOException::class)
    private fun copyDatabase() {
        val inputStream: InputStream = context.assets.open("databases/$DATABASE_NAME")
        val outputStream: OutputStream = FileOutputStream(dbPath)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Empty, database will be copied from assets
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    // Empty, database will be copied from assets
        if (oldVersion < newVersion) {
            copyDatabase()
        }

    }
}
