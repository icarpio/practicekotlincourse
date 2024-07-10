package com.example.tmdb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tmdb.R
import com.example.tmdb.data.FavoriteMovie
import com.example.tmdb.data.Movie
import com.example.tmdb.database.DatabaseHelper
import com.example.tmdb.databinding.ActivityDetailBinding
import com.example.tmdb.databinding.DialogAddFavoriteBinding
import com.example.tmdb.utils.Constants
import com.example.tmdb.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
    }
    private lateinit var binding: ActivityDetailBinding
    private lateinit var movie: Movie
    private lateinit var databaseHelper: DatabaseHelper

    private var favoriteMenuItem:MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseHelper = DatabaseHelper(this)

        val id = intent.getIntExtra("EXTRA_ID", -1)
        Log.e("MOVIE ID",id.toString())
        getMovieDetails(id)
    }


    private fun getMovieDetails(id:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getMovieById(id,Constants.API_KEY, Constants.SPANISH)
                if(response.body() != null){
                    runOnUiThread {
                        movie = response.body()!!
                        Log.e("MOVIE DETAIL", response.body().toString())
                    }
                }else {
                    Toast.makeText(this@DetailActivity, "Error al obtener los detalles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@DetailActivity, "Error al obtener los detalles", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createUI(movie:Movie){
        //binding.shBiographyTextView.text = superhero.biography.fullname
        //binding.shPublisherTextView.text = superhero.biography.publisher
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_activity_detail, menu)
        favoriteMenuItem = menu.findItem(R.id.action_favourite)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_favourite -> {
                showAddFavoriteDialog()
                true
            }
            R.id.action_show_favorites-> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddFavoriteDialog() {
        val binding = DialogAddFavoriteBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setTitle("¿Seguro que deseas añadir la película?")
            .setView(binding.root)
            .setPositiveButton("OK") { dialog, which ->
                val title = movie.title
                val image = Constants.URL_IMAGE + movie.poster_path
                val favorite = FavoriteMovie(-1, title, image, 0)

                // Lanzar corrutina para insertar la película
                CoroutineScope(Dispatchers.Main).launch {
                    databaseHelper.insertMovieWithCheckDuplicate(favorite, applicationContext)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }



}