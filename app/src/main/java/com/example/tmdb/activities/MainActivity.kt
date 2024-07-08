package com.example.tmdb.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.tmdb.R
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ActivityMainBinding
import com.example.tmdb.network.WebService
import com.example.tmdb.utils.Constants
import com.example.tmdb.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var dataset: List<Movie> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    private fun getBillBoard() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getBillboard(Constants.API_KEY)
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
                val response = RetrofitClient.webService.getPopulares(Constants.API_KEY)
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


}