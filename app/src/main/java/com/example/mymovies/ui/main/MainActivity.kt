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
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.mymovies.BuildConfig
import com.example.mymovies.R
import com.example.mymovies.data.location.DefaultLocationTracker
import com.example.mymovies.databinding.ActivityMainBinding
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.ui.detail.DetailActivity
import com.example.mymovies.util.Constants.Companion.DEFAULT_API_REGION
import com.example.mymovies.util.Constants.Companion.DEFAULT_LATITUDE
import com.example.mymovies.util.Constants.Companion.DEFAULT_LONGITUDE
import com.example.mymovies.util.Constants.Companion.DENIED
import com.example.mymovies.util.Constants.Companion.GRANTED
import com.example.mymovies.util.Constants.Companion.LOADING_DATA_ERROR
import com.example.mymovies.util.Constants.Companion.PERMANENTLY_DENIED
import com.example.mymovies.util.hasPermission
import com.example.mymovies.util.openAppSettings
import com.example.mymovies.util.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var defaultLocationTracker: DefaultLocationTracker

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var mLayoutManager: LayoutManager

    private val viewModel by viewModels<MainViewModel>()

    private val apiKey = BuildConfig.API_KEY
    private val accessToken = BuildConfig.ACCESS_TOKEN

    private var appName: String = ""
    private var permission: String = ""

    private val granted: () -> Unit = { handlingPermissionsGranted() }
    private val rationale: () -> Unit = { showDialog() }
    private val denied: () -> Unit = { handlingPermissionsDenied() }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> {
                granted()
                Log.d(TAG, "Permission: $permission, $GRANTED")
            }

            shouldShowRequestPermissionRationale(permission) -> {
                rationale()
                Log.d(TAG, "Permission: $permission, $DENIED")
            }

            else -> {
                denied()
                Log.d(TAG, "Permission: $permission, $PERMANENTLY_DENIED")
            }
        }
    }

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
                val location =
                    defaultLocationTracker.getLastLocation()
                        ?: defaultLocationTracker.getCurrentLocation()

                viewModel.apikey = apiKey
                viewModel.region = getRegionFromLocation(location)
                viewModel.language = getApiLanguage()
                viewModel.getPopularMoviesWithApiKey()

                showPopularMovies()
            }
        }

        Log.d(TAG, "Call service")
    }

    private fun requestPermission() {
        when {
            !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                permission = Manifest.permission.ACCESS_COARSE_LOCATION

                permissionLauncher.launch(permission)
            }

            !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                permission = Manifest.permission.ACCESS_FINE_LOCATION

                permissionLauncher.launch(permission)
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

                permissionLauncher.launch(permission)
            }

            Manifest.permission.ACCESS_FINE_LOCATION -> {
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
                    location?.latitude ?: DEFAULT_LATITUDE,
                    location?.longitude ?: DEFAULT_LONGITUDE,
                    1
                ) { addresses ->
                    result = addresses
                }
            } else {
                result = geocoder.getFromLocation(
                    location?.latitude ?: DEFAULT_LATITUDE,
                    location?.longitude ?: DEFAULT_LONGITUDE,
                    1
                )
            }
        }

        Log.i(TAG, "location: ${location?.latitude} latitude, ${location?.longitude} longitude")

        if (result?.isNotEmpty() == true) {
            return result?.get(0)?.countryCode
        }

        return DEFAULT_API_REGION
    }

    private fun getApiLanguage() = Locale.getDefault().language.lowercase()

    private fun showPopularMovies() {
        viewModel.movies.observe(this@MainActivity) { popularMovies ->
            if (popularMovies != null) {
                movieAdapter.setListOfMovies(popularMovies)
                showData()
            } else {
                Log.d("MainActivity", LOADING_DATA_ERROR)
            }
        }

    }

    private fun showData() {
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

    private fun navigateToDetailActivity(movie: Movie?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)

        startActivity(intent)
    }

}