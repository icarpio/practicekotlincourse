package com.example.tmdb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdb.data.Movie
import com.example.tmdb.databinding.ItemMovieBinding
import com.example.tmdb.utils.Constants.URL_IMAGE
import com.squareup.picasso.Picasso

class MovieAdapter(private val movieList: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            val posterImage = URL_IMAGE + movie.poster_path
            binding.nameMovieTextView.text = movie.original_title
            Picasso.get().load(posterImage).into(binding.movieImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}