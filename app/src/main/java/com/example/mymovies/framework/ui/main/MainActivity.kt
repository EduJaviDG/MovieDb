package com.example.mymovies.framework.ui.main

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mymovies.R
import com.example.mymovies.data.datasources.PermissionRequester
import com.example.mymovies.databinding.ActivityMainBinding
import com.example.mymovies.domain.Movie
import com.example.mymovies.framework.ui.detail.DetailActivity
import com.example.mymovies.util.openAppSettings
import com.example.mymovies.util.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var movieAdapter: MovieAdapter

    private val viewModel by viewModels<MainViewModel>()

    private val apiKey: String by lazy { getString(R.string.api_key) }
    private val accessToken: String by lazy { getString(R.string.access_token) }

    private val coarsePermission: PermissionRequester =
        PermissionRequester(this, Manifest.permission.ACCESS_COARSE_LOCATION)

    private val granted: () -> Unit = { callService() }
    private val rationale: () -> Unit = { showDialog() }
    private val denied: () -> Unit = { toast(getString(R.string.message_toast)) }

    private val movieListener = object : MovieClickListener {
        override fun onClickMovie(item: Movie?) {
            navigateToDetailActivity(item)
        }
    }

    companion object {
        const val DEFAULT_API_LANGUAGE = "en-US"
        const val DEFAULT_REGION = "US"
        const val SPANISH_LANGUAGE = "es"
        const val ENGLISH_LANGUAGE = "en"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClient()
        setPermissions()
        initRecycle()
        refreshLayout()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu,menu)

        return true
    }

    private fun initRecycle() {
        movieAdapter = MovieAdapter()
        binding.rvPopularMovies.adapter = movieAdapter

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

    private fun callService() {
        lifecycleScope.launch {
            viewModel.apikey = apiKey
            viewModel.region = getLocationClient()
            viewModel.language = getApiLanguage()
            viewModel.getPopularMovies()

            showPopularMovies()
        }
    }
    private fun setPermissions() {
        coarsePermission.setInfoPermission(granted, rationale, denied)
        coarsePermission.runWithPermission()

    }

    @SuppressLint("MissingPermission")
    private suspend fun getLocationClient(): String? = suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation.addOnCompleteListener { lastLocation ->
            if (lastLocation.result == null) {
                val cancellationToken = object : CancellationToken() {
                    override fun isCancellationRequested(): Boolean {
                        return false
                    }

                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }

                }

                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationToken
                )
                    .addOnCompleteListener { currentLocation ->
                        if (currentLocation.result == null) {
                            val locationRequest = LocationRequest()
                                .setInterval(60000L)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                            val locationCallBack = object : LocationCallback() {
                                override fun onLocationResult(p0: LocationResult?) {
                                    super.onLocationResult(p0)
                                    val locations = p0?.locations

                                    if (locations != null) {
                                        continuation.resume(getRegionFromLocation(locations[0]))
                                    }
                                }
                            }

                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallBack,
                                null
                            )

                        } else {
                            continuation.resume(getRegionFromLocation(currentLocation.result))
                        }
                    }

            } else {
                continuation.resume(getRegionFromLocation(lastLocation.result))
            }
        }
    }

    private fun getRegionFromLocation(location: Location): String? {
        val geocoder = Geocoder(this)
        var result: List<Address>? = listOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) { addresses ->
                result = addresses
            }
        } else {
            result = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
        }

        return result?.get(0)?.countryCode

    }

    private fun getApiLanguage(): String {
        val language = Locale.getDefault().language.lowercase()

        val apiLanguage = if (language == SPANISH_LANGUAGE) {
            "${language.lowercase()}-${language.uppercase()}"
        } else {
            DEFAULT_API_LANGUAGE
        }

        return apiLanguage
    }

    private fun showPopularMovies() {
        viewModel.movies.observe(this@MainActivity){ popularMovies ->
            if (popularMovies != null) {
                movieAdapter.setListOfMovies(popularMovies)
                showData()
            } else {
                Log.d("MainActivity", "ERROR LOADING DATA")
            }

        }

    }

    private fun showData(){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.shlLoading.visibility = GONE
            binding.rvPopularMovies.visibility = VISIBLE
        }, 4000)
    }

    private fun updateLayout() {
        recreate()
        binding.swRefresh.isRefreshing = false
    }

    private fun navigateToDetailActivity(movie: Movie?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)

        startActivity(intent)
    }

    private fun showDialog() {
        AlertDialog.Builder(this, R.style.Custom_Alert_Dialog)
            .setTitle(R.string.title_dialog)
            .setMessage(R.string.message_dialog)
            .setPositiveButton(R.string.title_positive_button_dialog, { _, _ -> openSettings() })
            .setNegativeButton(R.string.title_negative_button_dialog, { _, _ -> showSnackBar() })
            .setCancelable(false)
            .show()
    }

    private fun openSettings() {
        CoroutineScope(Dispatchers.Default).launch {
            delay(1000L)
            openAppSettings()
        }
    }

    private fun showSnackBar() {
        Snackbar.make(binding.root, R.string.message_snack, Snackbar.LENGTH_LONG)
            .setAction(R.string.title_action_snack, { actionClick() })
            .setActionTextColor(getColor(R.color.dark_pink))
            .show()
    }

    private fun actionClick() {}

}