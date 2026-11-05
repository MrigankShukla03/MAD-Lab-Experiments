package com.example.tripjournal.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tripjournal.models.Photo
import com.example.tripjournal.models.Trip

class TripDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTripsTable = """
            CREATE TABLE $TABLE_TRIPS (
                $COLUMN_TRIP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TRIP_TITLE TEXT NOT NULL,
                $COLUMN_TRIP_LOCATION TEXT NOT NULL,
                $COLUMN_TRIP_LAT REAL,
                $COLUMN_TRIP_LNG REAL,
                $COLUMN_TRIP_DATE TEXT NOT NULL,
                $COLUMN_TRIP_NOTES TEXT,
                $COLUMN_TRIP_COVER TEXT
            )
        """.trimIndent()

        val createPhotosTable = """
            CREATE TABLE $TABLE_PHOTOS (
                $COLUMN_PHOTO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PHOTO_TRIP_ID INTEGER,
                $COLUMN_PHOTO_URI TEXT NOT NULL,
                FOREIGN KEY($COLUMN_PHOTO_TRIP_ID) REFERENCES $TABLE_TRIPS($COLUMN_TRIP_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createTripsTable)
        db.execSQL(createPhotosTable)

        seedSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PHOTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRIPS")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }

    private fun seedSampleData(db: SQLiteDatabase) {
        val samples = listOf(
            Trip(1, "Goa Getaway", "Goa – Baga Beach", 15.5527, 73.7517, "10-01-2026", "Sun, sand, and wonderful beachside sunsets.", null),
            Trip(2, "Mountain Expedition", "Manali – Solang Valley", 32.2396, 77.1887, "15-02-2026", "Snow-capped peaks and thrilling adventure sports.", null),
            Trip(3, "Garden City Tour", "Bangalore – Lalbagh Botanical Garden", 12.9507, 77.5848, "20-03-2026", "Exploring heritage glass house and exotic flora.", null),
            Trip(4, "Royal Heritage", "Jaipur – Hawa Mahal", 26.9239, 75.8267, "12-04-2026", "Majestic pink city architecture and royal palaces.", null),
            Trip(5, "Palace Exploration", "Mysore – Mysore Palace", 12.3052, 76.6552, "05-05-2026", "Grand illuminated palace and rich cultural history.", null)
        )

        for (trip in samples) {
            val values = ContentValues().apply {
                put(COLUMN_TRIP_TITLE, trip.title)
                put(COLUMN_TRIP_LOCATION, trip.location)
                put(COLUMN_TRIP_LAT, trip.latitude)
                put(COLUMN_TRIP_LNG, trip.longitude)
                put(COLUMN_TRIP_DATE, trip.date)
                put(COLUMN_TRIP_NOTES, trip.notes)
                put(COLUMN_TRIP_COVER, trip.coverPhotoUri)
            }
            db.insert(TABLE_TRIPS, null, values)
        }
    }

    // --- TRIPS FUNCTIONS ---

    fun insertTrip(trip: Trip): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TRIP_TITLE, trip.title)
            put(COLUMN_TRIP_LOCATION, trip.location)
            put(COLUMN_TRIP_LAT, trip.latitude)
            put(COLUMN_TRIP_LNG, trip.longitude)
            put(COLUMN_TRIP_DATE, trip.date)
            put(COLUMN_TRIP_NOTES, trip.notes)
            put(COLUMN_TRIP_COVER, trip.coverPhotoUri)
        }
        val id = db.insert(TABLE_TRIPS, null, values)
        db.close()
        return id
    }

    fun getAllTrips(): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TRIPS ORDER BY $COLUMN_TRIP_ID DESC", null)

        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_ID)
            val titleIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_TITLE)
            val locIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_LOCATION)
            val latIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_LAT)
            val lngIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_LNG)
            val dateIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_DATE)
            val notesIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_NOTES)
            val coverIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_COVER)

            do {
                trips.add(
                    Trip(
                        id = cursor.getLong(idIdx),
                        title = cursor.getString(titleIdx),
                        location = cursor.getString(locIdx),
                        latitude = cursor.getDouble(latIdx),
                        longitude = cursor.getDouble(lngIdx),
                        date = cursor.getString(dateIdx),
                        notes = cursor.getString(notesIdx),
                        coverPhotoUri = cursor.getString(coverIdx)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return trips
    }

    fun getTripById(tripId: Long): Trip? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TRIPS WHERE $COLUMN_TRIP_ID = ?", arrayOf(tripId.toString()))
        var trip: Trip? = null

        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_ID)
            val titleIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_TITLE)
            val locIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_LOCATION)
            val latIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_LAT)
            val lngIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_LNG)
            val dateIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_DATE)
            val notesIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_NOTES)
            val coverIdx = cursor.getColumnIndexOrThrow(COLUMN_TRIP_COVER)

            trip = Trip(
                id = cursor.getLong(idIdx),
                title = cursor.getString(titleIdx),
                location = cursor.getString(locIdx),
                latitude = cursor.getDouble(latIdx),
                longitude = cursor.getDouble(lngIdx),
                date = cursor.getString(dateIdx),
                notes = cursor.getString(notesIdx),
                coverPhotoUri = cursor.getString(coverIdx)
            )
        }
        cursor.close()
        db.close()
        return trip
    }

    fun updateTrip(trip: Trip): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TRIP_TITLE, trip.title)
            put(COLUMN_TRIP_LOCATION, trip.location)
            put(COLUMN_TRIP_LAT, trip.latitude)
            put(COLUMN_TRIP_LNG, trip.longitude)
            put(COLUMN_TRIP_DATE, trip.date)
            put(COLUMN_TRIP_NOTES, trip.notes)
            put(COLUMN_TRIP_COVER, trip.coverPhotoUri)
        }
        val rows = db.update(TABLE_TRIPS, values, "$COLUMN_TRIP_ID = ?", arrayOf(trip.id.toString()))
        db.close()
        return rows
    }

    fun deleteTrip(tripId: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_TRIPS, "$COLUMN_TRIP_ID = ?", arrayOf(tripId.toString()))
        db.close()
        return rows
    }

    // --- PHOTOS FUNCTIONS ---

    fun insertPhoto(photo: Photo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PHOTO_TRIP_ID, photo.tripId)
            put(COLUMN_PHOTO_URI, photo.photoUri)
        }
        val id = db.insert(TABLE_PHOTOS, null, values)
        db.close()
        return id
    }

    fun getPhotosByTripId(tripId: Long): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_PHOTOS WHERE $COLUMN_PHOTO_TRIP_ID = ? ORDER BY $COLUMN_PHOTO_ID DESC",
            arrayOf(tripId.toString())
        )

        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndexOrThrow(COLUMN_PHOTO_ID)
            val tripIdIdx = cursor.getColumnIndexOrThrow(COLUMN_PHOTO_TRIP_ID)
            val uriIdx = cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI)

            do {
                photos.add(
                    Photo(
                        id = cursor.getLong(idIdx),
                        tripId = cursor.getLong(tripIdIdx),
                        photoUri = cursor.getString(uriIdx)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return photos
    }

    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PHOTOS ORDER BY $COLUMN_PHOTO_ID DESC", null)

        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndexOrThrow(COLUMN_PHOTO_ID)
            val tripIdIdx = cursor.getColumnIndexOrThrow(COLUMN_PHOTO_TRIP_ID)
            val uriIdx = cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI)

            do {
                photos.add(
                    Photo(
                        id = cursor.getLong(idIdx),
                        tripId = cursor.getLong(tripIdIdx),
                        photoUri = cursor.getString(uriIdx)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return photos
    }

    fun deletePhoto(photoId: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_PHOTOS, "$COLUMN_PHOTO_ID = ?", arrayOf(photoId.toString()))
        db.close()
        return rows
    }

    fun deletePhotosByTripId(tripId: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_PHOTOS, "$COLUMN_PHOTO_TRIP_ID = ?", arrayOf(tripId.toString()))
        db.close()
        return rows
    }

    companion object {
        private const val DATABASE_NAME = "TripJournal.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_TRIPS = "trips"
        const val COLUMN_TRIP_ID = "id"
        const val COLUMN_TRIP_TITLE = "title"
        const val COLUMN_TRIP_LOCATION = "location"
        const val COLUMN_TRIP_LAT = "latitude"
        const val COLUMN_TRIP_LNG = "longitude"
        const val COLUMN_TRIP_DATE = "date"
        const val COLUMN_TRIP_NOTES = "notes"
        const val COLUMN_TRIP_COVER = "cover_photo_uri"

        const val TABLE_PHOTOS = "photos"
        const val COLUMN_PHOTO_ID = "id"
        const val COLUMN_PHOTO_TRIP_ID = "trip_id"
        const val COLUMN_PHOTO_URI = "photo_uri"
    }
}
