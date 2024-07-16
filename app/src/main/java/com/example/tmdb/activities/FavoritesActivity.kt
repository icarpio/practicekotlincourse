package com.example.tmdb.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tmdb.adapters.FavoritesAdapter
import com.example.tmdb.data.FavoriteMovie
import com.example.tmdb.database.DatabaseHelper
import com.example.tmdb.databinding.ActivityFavoritesBinding
import com.example.tmdb.databinding.DialogAddFavoriteBinding
import com.example.tmdb.databinding.DialogChangeFavoriteBinding
import com.example.tmdb.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var favoriteList:List<FavoriteMovie>
    private lateinit var favoriteAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)
        favoriteList = databaseHelper.getAllMovies()

        // Configurar RecyclerView
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador de favoritos
        favoriteAdapter = FavoritesAdapter(favoriteList,
            { position ->
                showDeleteConfirmationDialog(position)
                loadData()
            },
            { position ->
                changeStatusFavorite(position)
                loadData()
            }
        )
        // Asignar el adaptador al RecyclerView
        binding.recyclerViewFavorites.adapter = favoriteAdapter
    }
    fun showDeleteConfirmationDialog(position: Int) {
        // Crea el AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Estás seguro de que deseas eliminar la pelicula?")
            .setPositiveButton("Sí") { dialog, id ->
                // Usuario hizo clic en "Sí", así que elimina la tarea
                Log.i("DELETE",favoriteList[position].toString())
                databaseHelper.deleteMovie(favoriteList[position])
                Toast.makeText(
                    this,
                    "Pelicula borrada correctamente: ${favoriteList[position].title}",
                    Toast.LENGTH_SHORT
                ).show()
                loadData()
            }
            .setNegativeButton("No") { dialog, id ->
                // Usuario hizo clic en "No", así que solo cierra el diálogo
                dialog.dismiss()
            }
        // Muestra el AlertDialog
        builder.create().show()
    }


    private fun loadData() {
        favoriteList = databaseHelper.getAllMovies()
        favoriteAdapter.updateData(favoriteList)
    }

    fun changeStatusFavorite(position: Int){
        val fav = favoriteList[position]
        val binding = DialogChangeFavoriteBinding.inflate(layoutInflater)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Seguro que quieres modificar el estado de la pelicula?")
            .setView(binding.root)
            .setPositiveButton("OK") { dialog, which ->
                if(fav.viewMovie == 0) {
                    fav.viewMovie = 1
                }else {
                    fav.viewMovie = 0
                }
                databaseHelper.updateMovie(fav)
                loadData()
                Toast.makeText(
                    this,
                    "Pelicula actualizada correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                loadData()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

}