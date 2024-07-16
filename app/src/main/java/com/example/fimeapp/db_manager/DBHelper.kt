package com.example.fimeapp.db_manager

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 14
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

    fun read(tableName: String, columns: Array<String>, selection: String? = null, selectionArgs: Array<String>? = null, orderBy: String? = null): List<Map<String, Any?>>  {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null)
        val results = mutableListOf<Map<String, Any?>>()

        cursor.use { // Use 'use' to automatically close the cursor
            if (cursor.moveToFirst()) {
                do {
                    val row = mutableMapOf<String, Any?>()
                    for (column in columns) {
                        val index = cursor.getColumnIndex(column)
                        if (index != -1) {
                            row[column] = when (cursor.getType(index)) {
                                Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(index)
                                Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(index)
                                Cursor.FIELD_TYPE_STRING -> cursor.getString(index)
                                Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(index)
                                Cursor.FIELD_TYPE_NULL -> null
                                else -> null
                            }
                        }
                    }
                    results.add(row)
                } while (cursor.moveToNext())
            }
        }

        return results
    }

}
