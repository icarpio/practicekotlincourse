package com.example.tmdb.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdb.activities.DetailActivity
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ItemMovieBinding
import com.example.tmdb.utils.Constants.URL_IMAGE
import com.squareup.picasso.Picasso

class MovieAdapter(
    private var dataset:List<Movie> = emptyList(),
    //Navergar desde Main a Detail
    private val onItemSelected: (Int) -> Unit):
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie, onItemSelected: (Int) ->Unit) {
            val posterImage = URL_IMAGE + movie.poster_path
            binding.nameMovieTextView.text = movie.original_title
            Picasso.get().load(posterImage).into(binding.movieImageView)

            //Pasa el id del superheroe al Adapter
            binding.root.setOnClickListener {
                onItemSelected(movie.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        //val movie = movieList[position]
        holder.bind(dataset[position],onItemSelected)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun updateData(dataset:List<Movie>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }
}