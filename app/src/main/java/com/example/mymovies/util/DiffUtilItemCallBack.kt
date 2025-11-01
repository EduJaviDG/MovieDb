package com.example.mymovies.util

import androidx.recyclerview.widget.DiffUtil
import com.example.mymovies.domain.model.Movie


class DiffUtilItemCallBack: DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.hashCode() == newItem.hashCode()

}