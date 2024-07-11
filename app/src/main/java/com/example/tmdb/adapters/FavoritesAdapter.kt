package com.example.tmdb.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdb.R
import com.example.tmdb.data.FavoriteMovie
import com.example.tmdb.databinding.ItemFavoriteMovieBinding
import com.squareup.picasso.Picasso


class FavoritesAdapter (private var favoriteMovies:List<FavoriteMovie>,
                        private val onItemDeleteClickListener: (Int) -> Unit,
                        private val onItemChangeClickListener: (Int) -> Unit
    )
    : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>(){
    class FavoriteViewHolder(val binding: ItemFavoriteMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun render(favoriteMovie: FavoriteMovie){
            val green = binding.root.context.getColor(R.color.success)
            val bitter = binding.root.context.getColor(R.color.black)
            binding.titleTextView.text = favoriteMovie.title
            Picasso.get().load(favoriteMovie.image).into(binding.movieImageView)
            if(favoriteMovie.viewMovie == 1){
                binding.viewMovieImageView.setColorFilter(green)
                binding.viewMovieImageView.setImageResource(R.drawable.eye_open)
            }else {
                binding.viewMovieImageView.setColorFilter(bitter)
                binding.viewMovieImageView.setImageResource(R.drawable.eye_slash)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }
    override fun getItemCount(): Int = favoriteMovies.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.render(favoriteMovies[position])
        holder.binding.deleteButton.setOnClickListener {
            onItemDeleteClickListener(position)
        }
        holder.binding.viewMovieImageView.setOnClickListener {
            onItemChangeClickListener(position)
        }
    }
    fun updateData(dataSet: List<FavoriteMovie>) {
        this.favoriteMovies = dataSet
        notifyDataSetChanged()
    }



}