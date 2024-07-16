package com.example.tmdb.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View

import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tmdb.R
import com.example.tmdb.activities.DetailActivity.Companion.EXTRA_ID
import com.example.tmdb.adapters.MovieAdapter
import com.example.tmdb.data.ColorDialog
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ActivityMainBinding
import com.example.tmdb.utils.Constants
import com.example.tmdb.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var movieAdapter: MovieAdapter
    var dataset: List<Movie> = emptyList()

    lateinit var sharedPreferences: SharedPreferences

    // SharedPreferences
    //val savedColor = sharedPreferences.getInt("selected_color", defaultColor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        movieAdapter = MovieAdapter(onItemSelected = { movieId ->
            navigatetoDetail(movieId)
        })
        binding.recyclerView.adapter = movieAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        // Set click listeners for the buttons
        binding.btnGetBillBoard.setOnClickListener {
            getBillBoard()

        }

        binding.btnGetPopulares.setOnClickListener {
            getPopulares()
        }

        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val savedColor = sharedPreferences.getInt("selected_color", R.color.colorPrimary)

        setButtonsColor(getColor(savedColor))
    }

    fun setButtonsColor (color: Int) {
        binding.btnGetBillBoard.setBackgroundColor(color)
        binding.btnGetPopulares.setBackgroundColor(color)
    }

    private fun getBillBoard() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getBillboard(Constants.API_KEY,Constants.SPANISH)
                val responseBody = response.body()
                if (responseBody != null) {
                    dataset = responseBody.results.sortedByDescending { it.release_date }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_activity_main, menu)

        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("onQueryTextSubmit: $query")
                searchByName(query.orEmpty())
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                println("onQueryTextChange: $newText")
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            R.id.action_change_color -> {
                // Mostrar el diálogo de selección de color
                val colors = arrayOf(
                    R.color.colorPrimary,
                    R.color.Triadic1,
                    R.color.Triadic2
                )
                val colorDialog = ColorDialog(this, colors) { selectedColor ->
                    sharedPreferences.edit().putInt("selected_color", selectedColor).apply()
                    setButtonsColor(getColor(selectedColor))
                }
                colorDialog.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.searchbyname(query, Constants.API_KEY, Constants.SPANISH)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            dataset = responseBody.results
                            if (dataset.isNullOrEmpty()) {
                                Log.e("SEARCH", "Dataset is null or empty: $dataset")
                            } else {
                                movieAdapter.updateData(dataset)
                                binding.recyclerView.visibility = View.VISIBLE
                            }
                        } else {
                            Log.e("SEARCH", "Response body is null")
                        }
                    } else {
                        Log.e("SEARCH", "Response unsuccessful: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SEARCH", "Exception caught: ${e.message}")
            }
        }
    }
}