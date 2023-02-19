package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.Target

class MoviesAdapter(private val movies: MutableList<Movie>?,context: Context,click : clickadapter) :
    RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {
    var context :Context = context
    val clickitem = click

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val yearTextView: TextView = itemView.findViewById(R.id.yearTextView)
        val posterImageView: ImageView = itemView.findViewById(R.id.movie_poster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies?.get(position)
        holder.titleTextView.text = movie?.title
        holder.yearTextView.text = movie?.year
        val url : String? = movie?.posterUrl
        // Load the movie poster image into the ImageView using a library like Picasso or Glide
        // For example:


        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.placeholder_poster)
            .thumbnail(0.1f)
            .into(holder.posterImageView)


        holder.itemView.setOnClickListener{

            movie?.imdbId?.let { it1 -> clickitem.onItemClick(it1) }


        }

    }


    override fun getItemCount(): Int =movies?.size?:0


    fun addMovies(newMovies: List<Movie>) {
        val initialSize = movies?.size
        movies?.addAll(newMovies)
        if (initialSize != null) {
            notifyItemRangeInserted(initialSize, newMovies.size)
        }
    }

    fun clearMovies() {
        movies?.clear()
        notifyDataSetChanged()
    }

}


