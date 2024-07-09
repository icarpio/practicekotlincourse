package com.example.tmdb.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.example.tmdb.R
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ActivityDetailBinding
import com.example.tmdb.utils.Constants
import com.example.tmdb.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
    }
    private lateinit var binding: ActivityDetailBinding

    private lateinit var movie: Movie

    private var favoriteMenuItem:MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                        //createUI(response.body()!!)
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

                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

}