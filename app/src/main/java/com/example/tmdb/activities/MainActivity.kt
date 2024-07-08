package com.example.tmdb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tmdb.activities.DetailActivity.Companion.EXTRA_ID
import com.example.tmdb.adapters.MovieAdapter
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ActivityMainBinding
import com.example.tmdb.utils.Constants
import com.example.tmdb.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var movieAdapter: MovieAdapter
    var dataset: List<Movie> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieAdapter = MovieAdapter(emptyList()) {
            navigatetoDetail(dataset[it].id)
        }
        binding.recyclerView.adapter = movieAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        // Set click listeners for the buttons
        binding.btnGetBillBoard.setOnClickListener {
            getBillBoard()
        }

        binding.btnGetPopulares.setOnClickListener {
            getPopulares()
        }
    }
    private fun getBillBoard() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getBillboard(Constants.API_KEY,Constants.SPANISH)
                Log.e("API", response.toString())
                val responseBody = response.body()

                if (responseBody != null) {
                    dataset = responseBody.results.sortedByDescending { it.vote_average }
                } else {
                    Log.e("API", "Response body is null")
                }
                // Llamada en hilo principal. En este hilo es donde se pueden gestionar los activities
                runOnUiThread {
                    if (dataset.isNullOrEmpty()) {
                        Log.e("API", "Dataset is null or empty: $dataset")
                    } else {
                        movieAdapter.updateData(dataset)
                        binding.recyclerView.visibility = View.VISIBLE
                        Log.e("API", "Results found: $dataset")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("API", "Exception caught: ${e.message}")
                }
            }
        }
    }

    private fun getPopulares() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getPopulares(Constants.API_KEY,Constants.SPANISH)
                Log.d("RESPONSE", response.toString())
                val responseBody = response.body()

                if (responseBody != null) {
                    dataset = responseBody.results
                } else {
                    Log.e("API", "Response body is null")
                }
                // Llamada en hilo principal. En este hilo es donde se pueden gestionar los activities
                runOnUiThread {
                    if (dataset.isNullOrEmpty()) {
                        Log.e("API", "Dataset is null or empty: $dataset")
                    } else {
                        movieAdapter.updateData(dataset)
                        binding.recyclerView.visibility = View.VISIBLE
                        Log.e("API", "Results found: $dataset")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("API", "Exception caught: ${e.message}")
                }
            }
        }
    }

    //Navergar desde Main a Detail
    private fun navigatetoDetail(id: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_ID, id)
        startActivity(intent)
    }

}