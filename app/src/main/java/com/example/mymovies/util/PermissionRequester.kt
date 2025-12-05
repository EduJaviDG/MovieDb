package com.example.mymovies.util

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mymovies.ui.main.MainActivity

class PermissionRequester(activity: ComponentActivity, private val permission: String) {
    companion object{
        const val TAG = "PermissionRequester"
    }

    private var onGranted: () -> Unit = {}
    private var onRationale: () -> Unit = {}
    private var onDenied: () -> Unit = {}


    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()){ isGranted ->
        when{
            isGranted ->{
                onGranted()
                Log.d(TAG, "Permission: $permission, ${Constants.GRANTED}")
            }
            activity.shouldShowRequestPermissionRationale(permission) ->{
                onRationale()
                Log.d(TAG, "Permission: $permission, ${Constants.DENIED}")

            }
            else ->{
                onDenied()
                Log.d(TAG, "Permission: $permission, ${Constants.PERMANENTLY_DENIED}")
            }
        }
    }

    fun setInfoPermission(granted: () -> Unit, rationale: () -> Unit ,denied: () -> Unit){
        onGranted = granted
        onRationale = rationale
        onDenied = denied
    }

    fun runWithPermission() = permissionLauncher.launch(permission)


}
