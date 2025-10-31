package com.example.mymovies.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymovies.R
import com.example.mymovies.databinding.ItemMovieBinding
import com.example.mymovies.domain.model.Movie

class MovieAdapter(): RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    private var movieList: List<Movie>? = null
    private var movieListener: MovieClickListener? = null

    fun setListOfMovies(list: List<Movie>?){
        movieList = list

        notifyItemRangeInserted(0, movieList?.size ?: 0)
    }

    fun setListener(listener: MovieClickListener){
        movieListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = movieList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = movieList?.get(position)

        holder.bind(item, movieListener)
    }

    class ViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {

        companion object {
            private const val baseImageUrl = "https://image.tmdb.org/t/p/w185/"
        }

        fun bind(item: Movie?, listener: MovieClickListener?){
            binding.tvTitleMovie.text = item?.title
            Glide.with(itemView)
                .load(baseImageUrl + item?.posterPath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivCoverMovie)

            itemView.setOnClickListener {
                listener?.onClickMovie(item)
            }
        }
    }
}

interface MovieClickListener{
    fun onClickMovie(item: Movie?)
}