package com.example.mymovies.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.widget.Toast
import androidx.core.text.bold

inline fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


inline fun Context.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:$packageName")
    }.let(::startActivity)
}

inline fun SpannableStringBuilder.appendInfo(context: Context, stringRes: Int, value: String?){
    bold {
        append(context.resources.getString(stringRes))
        append(": ")
        appendLine(value)
    }
}