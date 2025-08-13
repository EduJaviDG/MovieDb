package com.example.mymovies.framework.ui.detail

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import com.bumptech.glide.Glide
import com.example.mymovies.R
import com.example.mymovies.databinding.DetailActivityBinding
import com.example.mymovies.domain.Movie
import com.example.mymovies.util.appendInfo


class DetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_MOVIE = "DetailActivity::title"
        const val URL = "https://image.tmdb.org/t/p/w780/"
    }

    private lateinit var binding: DetailActivityBinding
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_MOVIE, Movie::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_MOVIE)
        }

        showDetailMovie()
    }

    private fun showDetailMovie(){
        if (movie != null){
            supportActionBar?.title = movie?.title
            binding.tvDetailSummary.text = movie?.overview
            Glide
                .with(this)
                .load(URL + movie?.backdropPath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivDetailCoverMovie)

            bindDetailInfo()
            fabListener()

        } else {
            Log.d("DetailActivity","ERROR LOADING MOVIE")
        }
    }

    private fun bindDetailInfo(){
        binding.tvDetailInfo.text = buildSpannedString {
            appendInfo(this@DetailActivity, R.string.original_language, movie?.originalLanguage)
            appendInfo(this@DetailActivity, R.string.original_title, movie?.originalTitle)
            appendInfo(this@DetailActivity, R.string.title, movie?.title)
            appendInfo(this@DetailActivity, R.string.release_date, movie?.releaseDate)
            appendInfo(this@DetailActivity, R.string.popularity, movie?.popularity.toString())
            appendInfo(this@DetailActivity, R.string.vote_average, movie?.voteAverage.toString())
        }
    }

    private fun fabListener(){
        var like = false

        binding.fabLike.setOnClickListener {
            if(!like){
                binding.fabLike.setImageResource(R.drawable.baseline_favorite)
                like = true
            } else {
                binding.fabLike.setImageResource(R.drawable.outline_favorite)
                like = false
            }
        }
    }

}