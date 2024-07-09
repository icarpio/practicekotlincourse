package com.example.tmdb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tmdb.R
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ActivityDetailBinding
import com.example.tmdb.utils.Constants
import com.example.tmdb.utils.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
    }
    private lateinit var binding: ActivityDetailBinding

    private lateinit var movie: Movie
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


}