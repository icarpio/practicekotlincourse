package com.example.tmdb.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.tmdb.data.FavoriteMovie

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private const val DATABASE_VERSION = 1

        // Tabla de peliculas favoritas
        private const val TABLE_MOVIES = "movies"
        private const val COLUMN_MOVIE_ID = "id"
        private const val COLUMN_MOVIE_TITLE = "title"
        private const val COLUMN_IMAGE= "image"
        private const val COLUMN_MOVIE_VIEW= "movie_view"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableFavorites = """
            CREATE TABLE $TABLE_MOVIES (
                $COLUMN_MOVIE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MOVIE_TITLE TEXT,
                $COLUMN_IMAGE TEXT,
                $COLUMN_MOVIE_VIEW INTEGER DEFAULT 0
            )
        """.trimIndent()
        db?.execSQL(createTableFavorites)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }


    fun insertMovie(title: String, image: String,movieView:Int): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_MOVIE_TITLE,title )
            put(COLUMN_IMAGE, image)
            put(COLUMN_MOVIE_VIEW, movieView)
        }
        val result = db.insert(TABLE_MOVIES, null, contentValues)
        return result != -1L
    }
    //Actulizar favorito
    fun updateMovie(favoriteMovie: FavoriteMovie): Boolean {
        val db = writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put(COLUMN_MOVIE_TITLE, favoriteMovie.title)
                put(COLUMN_IMAGE, favoriteMovie.image)
            }
            val result = db.update(
                TABLE_MOVIES,
                contentValues,
                "${COLUMN_MOVIE_ID} = ?",
                arrayOf(favoriteMovie.id.toString())
            )
            Log.d("TaskUpdate", "Task updated with id: ${favoriteMovie.id}, Result: $result")
            result > 0  // Retorna true si al menos una fila fue actualizada
        } catch (e: Exception) {
            Log.e("TaskUpdate", "Error updating task", e)
            false
        } finally {
            db.close()
        }
    }

    // Eliminar favorito
    fun deleteMovie(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_MOVIES, "$COLUMN_MOVIE_ID = ?", arrayOf(id.toString()))
        return result > 0
    }

    //Obtener todos los favoritos
    fun getAllMovies(): List<FavoriteMovie> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM " + TABLE_MOVIES, null)
        var favorites = mutableListOf<FavoriteMovie>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_TITLE))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
                val movieView = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_VIEW))
                val favavorite = FavoriteMovie(id, title, image,movieView)
                favorites.add(favavorite)
                // Imprimir en el log
                Log.d("TaskRecord", "ID: $id, Title: $title, ImageUrl: $image")

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return favorites
    }
}