package com.example.tmdb.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Handler
import android.os.Looper
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import com.example.tmdb.data.FavoriteMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
    fun insertMovie(favorite: FavoriteMovie) {
            val db = writableDatabase
            // Verificar si la película ya existe
            val cursor = db.query(
                TABLE_MOVIES,
                arrayOf(COLUMN_MOVIE_ID),
                "$COLUMN_MOVIE_TITLE = ?",
                arrayOf(favorite.title),
                null,
                null,
                null
            )
            val exists = cursor.count > 0
            cursor.close()
            Log.d("DB - INSERT", "MoViE ExiTS")
            if (exists) {
                // Mostrar Toast o registrar que la película ya existe (en el hilo principal)
            } else {
                // Insertar la película si no existe
                val contentValues = ContentValues().apply {
                    put(COLUMN_MOVIE_TITLE, favorite.title)
                    put(COLUMN_IMAGE, favorite.image)
                    put(COLUMN_MOVIE_VIEW, favorite.viewMovie)
                }
                val result = db.insert(TABLE_MOVIES, null, contentValues)
                favorite.id = result.toInt()
                Log.d("DB - INSERT", "Inserted movie with ID: ${favorite.id}")
            }
    }
    fun checkMovie(){

    }
    //Actulizar favorito
    fun updateMovie(favorite: FavoriteMovie): Boolean {
        val db = writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put(COLUMN_MOVIE_TITLE, favorite.title)
                put(COLUMN_IMAGE, favorite.image)
                put(COLUMN_MOVIE_VIEW, favorite.viewMovie)
            }
            val result = db.update(
                TABLE_MOVIES,
                contentValues,
                "${COLUMN_MOVIE_ID} = ?",
                arrayOf(favorite.id.toString())
            )
            Log.d("DB - Update", "Movie updated with id: ${favorite.id}, Result: $result")
            result > 0  // Retorna true si al menos una fila fue actualizada
        } catch (e: Exception) {
            Log.e("DB - Update", "Error updating movie", e)
            false
        } finally {
            db.close()
        }
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
                val favorite = FavoriteMovie(id, title, image,movieView)
                favorites.add(favorite)
                // Imprimir en el log
                Log.d("Favorite Movie", "ID: $id, Title: $title, ImageUrl: $image")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return favorites
    }

    // Eliminar favorito

    suspend fun insertMovieWithCheckDuplicate(favorite: FavoriteMovie, context: Context) {
        withContext(Dispatchers.IO) {
            val db = writableDatabase
            // Verificar si la película ya existe
            val cursor = db.query(
                TABLE_MOVIES,
                arrayOf(COLUMN_MOVIE_ID),
                "$COLUMN_MOVIE_TITLE = ?",
                arrayOf(favorite.title),
                null,
                null,
                null
            )
            val exists = cursor.count > 0
            cursor.close()

            if (exists) {
                // Mostrar Toast en el hilo principal
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "La película '${favorite.title}' ya está en tus favoritos.", Toast.LENGTH_SHORT).show()
                }
                // Opcional: Puedes registrar el mensaje en el log
                // Log.d("DB - INSERT", "Movie with title ${favorite.title} already exists.")
            } else {
                // Insertar la película si no existe
                val contentValues = ContentValues().apply {
                    put(COLUMN_MOVIE_TITLE, favorite.title)
                    put(COLUMN_IMAGE, favorite.image)
                    put(COLUMN_MOVIE_VIEW, favorite.viewMovie)
                }
                val result = db.insert(TABLE_MOVIES, null, contentValues)
                favorite.id = result.toInt()
                // Log.d("DB - INSERT", "Inserted movie with ID: ${favorite.id}")
            }
        }
    }

    fun deleteMovie(favorite: FavoriteMovie) {
        val db = writableDatabase
        val deletedRows = db.delete(TABLE_MOVIES, "$COLUMN_MOVIE_ID = ?", arrayOf(favorite.id.toString()))
        Log.i("DELETE", "Deleted: $deletedRows row(s)")
        db.close()
    }


}