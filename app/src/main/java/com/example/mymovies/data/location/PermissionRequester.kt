package com.example.mymovies.data.location

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionRequester(activity: ComponentActivity, private val permission: String) {

    private var onGranted: () -> Unit = {}
    private var onRationale: () -> Unit = {}
    private var onDenied: () -> Unit = {}


    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()){ isGranted ->
        when{
            isGranted -> onGranted()
            activity.shouldShowRequestPermissionRationale(permission) -> onRationale()
            else -> onDenied()
        }
    }

    fun setInfoPermission(granted: () -> Unit, rationale: () -> Unit ,denied: () -> Unit){
        onGranted = granted
        onRationale = rationale
        onDenied = denied
    }

    fun runWithPermission() = permissionLauncher.launch(permission)


}
