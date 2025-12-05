package com.example.mymovies.ui.main

import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.mymovies.BuildConfig
import com.example.mymovies.R
import com.example.mymovies.data.location.DefaultLocationTracker
import com.example.mymovies.data.mappers.toDomainMovie
import com.example.mymovies.databinding.ActivityMainBinding
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.ui.detail.DetailActivity
import com.example.mymovies.util.Constants.Companion.DEFAULT_API_REGION
import com.example.mymovies.util.Constants.Companion.PAGE_SIZE
import com.example.mymovies.util.LayoutManagerType.GRID_LAYOUT
import com.example.mymovies.util.MockLocationProvider
import com.example.mymovies.util.PaginationScrollListener
import com.example.mymovies.util.PermissionRequester
import com.example.mymovies.util.Resource
import com.example.mymovies.util.hasPermission
import com.example.mymovies.util.openAppSettings
import com.example.mymovies.util.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var defaultLocationTracker: DefaultLocationTracker

    @Inject
    lateinit var mockLocationProvider: MockLocationProvider

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var mLayoutManager: LayoutManager

    private val viewModel by viewModels<MainViewModel>()

    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

    private var totalMovies: Int? = null
    private var currentPage: Int? = null

    private val apiKey = BuildConfig.API_KEY
    private val accessToken = BuildConfig.ACCESS_TOKEN

    private var appName: String = ""
    private var permission: String = ""

    private val coarsePermission: PermissionRequester =
        PermissionRequester(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val finePermission: PermissionRequester =
        PermissionRequester(this, Manifest.permission.ACCESS_FINE_LOCATION)

    private val granted: () -> Unit = { handlingPermissionsGranted() }
    private val rationale: () -> Unit = { showDialog() }
    private val denied: () -> Unit = { handlingPermissionsDenied() }

    private val movieListener = object : MovieClickListener {
        override fun onClickMovie(item: Movie?) {
            navigateToDetailActivity(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appName = getString(R.string.app_name)

        initClient()
        setPermission()
        requestPermission()
        initRecycle()
        refreshLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    private fun initRecycle() {
        movieAdapter = MovieAdapter()
        mLayoutManager = GridLayoutManager(this, 3)

        binding.rvPopularMovies.apply {
            adapter = movieAdapter
            layoutManager = mLayoutManager
            addOnScrollListener(object : PaginationScrollListener(GRID_LAYOUT, mLayoutManager) {
                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun totalItems(): Int? {
                    return totalMovies
                }

                override fun loadMoreItems() {
                    callService()
                }

            })
        }

        movieAdapter.setListener(movieListener)

    }

    private fun initClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()

    }

    private fun refreshLayout() {
        binding.swRefresh.setOnRefreshListener {
            updateLayout()
        }
    }

    private fun provideMockLocation() {
        mockLocationProvider.enableProvider()
        //Helsinki
        mockLocationProvider.pushLocation(lat = 60.192059, lon = 24.945831)
    }

    private fun setPermission() {
        coarsePermission.setInfoPermission(granted, rationale, denied)
        finePermission.setInfoPermission(granted, rationale, denied)
    }

    private fun callService(defaultRegion: String? = null) {
        val headers = mapOf<String, String>(
            "accept" to "application/json",
            "Authorization" to accessToken
        )

        if (defaultRegion != null) {
            lifecycleScope.launch {
                viewModel.apikey = apiKey
                viewModel.region = defaultRegion
                viewModel.language = getApiLanguage()
                viewModel.getPopularMoviesWithApiKey()

                showPopularMovies()
            }
        } else {
            lifecycleScope.launch {
                val location = defaultLocationTracker.getLastLocation()
                    ?: defaultLocationTracker.getCurrentLocation()

                viewModel.apikey = apiKey
                viewModel.region = getRegionFromLocation(location)
                viewModel.language = getApiLanguage()
                viewModel.getPopularMoviesWithApiKey()

                showPopularMovies()
            }
        }
    }

    private fun requestPermission() {
        when {
            !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                permission = Manifest.permission.ACCESS_COARSE_LOCATION
                coarsePermission.runWithPermission()
            }

            !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                permission = Manifest.permission.ACCESS_FINE_LOCATION
                finePermission.runWithPermission()
            }

            else -> {
                callService(DEFAULT_API_REGION)
            }
        }
    }

    private fun handlingPermissionsGranted() {
        when (permission) {
            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                permission = Manifest.permission.ACCESS_FINE_LOCATION
                finePermission.runWithPermission()
            }

            Manifest.permission.ACCESS_FINE_LOCATION -> {
                provideMockLocation()
                callService()
            }
        }
    }

    private fun handlingPermissionsDenied() {
        toast(getString(R.string.message_toast, appName))

        callService(DEFAULT_API_REGION)
    }

    private suspend fun getRegionFromLocation(location: Location?): String? {
        val geocoder = Geocoder(this)
        var result: List<Address>? = listOf()

        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0,
                    1
                ) { addresses ->
                    result = addresses
                }
            } else {
                result = geocoder.getFromLocation(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0,
                    1
                )
            }
        }

        Log.d(TAG, "location: ${location?.latitude} latitude, ${location?.longitude} longitude")

        if (result?.isNotEmpty() == true) {
            val region = result?.get(0)?.countryCode
            Log.d(TAG, "region: $region")

            return region
        }

        return DEFAULT_API_REGION
    }

    private fun getApiLanguage() = Locale.getDefault().language.lowercase()

    private fun showPopularMovies() {
        viewModel.popularMovies.observe(this@MainActivity) { response ->
            when (response) {
                is Resource.Error -> {
                    Log.d(TAG, response.message.toString())
                }

                is Resource.Loading -> {
                    if (currentPage != null) {
                        showLoading()
                    } else {
                        isLoading = true
                    }
                }

                is Resource.Success -> {
                    if (currentPage != null) {
                        hideLoading()
                    } else {
                        hideShimmer()
                    }

                    response.data?.let { data ->
                        val movies = data.movies?.map { it.toDomainMovie() }
                        movieAdapter.setListOfMovies(movies)

                        totalMovies = movies?.size
                        currentPage = data.page
                        isLastPage = checkLastPage(data.totalPages)

                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.pbPagination.visibility = VISIBLE
        isLoading = true

    }

    private fun hideLoading() {
        binding.pbPagination.visibility = INVISIBLE
        isLoading = false

    }

    private fun hideShimmer() {
        isLoading = false

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shlLoading.visibility = GONE
            binding.rvPopularMovies.visibility = VISIBLE
        }, 4000)

    }

    private fun updateLayout() {
        recreate()
        binding.swRefresh.isRefreshing = false
    }

    private fun showDialog() {
        AlertDialog.Builder(this, R.style.Custom_Alert_Dialog)
            .setTitle(R.string.title_dialog)
            .setMessage(R.string.message_dialog)
            .setPositiveButton(R.string.title_positive_button_dialog, { _, _ -> openAppSettings() })
            .setNegativeButton(R.string.title_negative_button_dialog, { _, _ -> showSnackBar() })
            .setCancelable(false)
            .show()
    }

    private fun showSnackBar() {
        Snackbar.make(binding.root, R.string.message_snack, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.title_action_snack) { actionClick() }
            .setActionTextColor(getColor(R.color.dark_pink))
            .show()
    }

    private fun actionClick() = callService(DEFAULT_API_REGION)

    private fun checkLastPage(pages: Int?): Boolean {
        val totalPages = (pages ?: 0) / PAGE_SIZE + 2

        return viewModel.page == totalPages
    }

    private fun navigateToDetailActivity(movie: Movie?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)

        startActivity(intent)
    }

}