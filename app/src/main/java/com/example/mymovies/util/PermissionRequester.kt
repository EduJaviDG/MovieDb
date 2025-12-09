package com.example.mymovies.util

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mymovies.ui.main.MainActivity

class PermissionRequester(activity: ComponentActivity, private val permission: String) {
    companion object{
        const val TAG = "PermissionRequester"
    }

    private var infoPermissionListener: InfoPermissionListener? = null

    private val permissionLauncher = activity.registerActivityResultLauncher(
       contract = ActivityResultContracts.RequestPermission(),
        callback = { isGranted ->
        when{
            isGranted ->{
                infoPermissionListener?.onGranted()
                Log.d(TAG, "Permission: $permission, ${Constants.GRANTED}")
            }
            activity.shouldShowRequestPermissionRationale(permission) ->{
                infoPermissionListener?.onRationale()
                Log.d(TAG, "Permission: $permission, ${Constants.DENIED}")

            }
            else ->{
                infoPermissionListener?.onDenied()
                Log.d(TAG, "Permission: $permission, ${Constants.PERMANENTLY_DENIED}")
            }
        }
    })

    fun setListener(listener: InfoPermissionListener) {
        infoPermissionListener = listener
    }

    fun runWithPermission() = permissionLauncher.launch(permission)

    fun unregister() = permissionLauncher.unregister()

}

interface InfoPermissionListener {
    fun onGranted()
    fun onRationale()
    fun onDenied()
}
