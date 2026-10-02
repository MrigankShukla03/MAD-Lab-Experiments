package com.example.madlab.experiment9

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_STUDENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_USN TEXT NOT NULL,
                $COLUMN_COURSE TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    // CRUD - Create
    fun insertRecord(name: String, usn: String, course: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_USN, usn)
            put(COLUMN_COURSE, course)
        }
        val id = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return id
    }

    // CRUD - Read
    fun getAllRecords(): List<StudentRecord> {
        val records = mutableListOf<StudentRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_STUDENTS ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME)
            val usnIndex = cursor.getColumnIndexOrThrow(COLUMN_USN)
            val courseIndex = cursor.getColumnIndexOrThrow(COLUMN_COURSE)

            do {
                val record = StudentRecord(
                    id = cursor.getLong(idIndex),
                    name = cursor.getString(nameIndex),
                    usn = cursor.getString(usnIndex),
                    course = cursor.getString(courseIndex)
                )
                records.add(record)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return records
    }

    // CRUD - Delete Single Record
    fun deleteRecord(id: Long): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_STUDENTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    // CRUD - Delete All Records
    fun deleteAllRecords() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_STUDENTS")
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "StudentSupport.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_STUDENTS = "students"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_USN = "usn"
        private const val COLUMN_COURSE = "course"
    }
}
