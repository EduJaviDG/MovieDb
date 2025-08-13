package com.example.mymovies.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String>?{
        val listType: Type = object : TypeToken<List<String?>?>() {}.type

        return try {
            Gson().fromJson(value, listType)

        } catch (e: Exception) {
            emptyList()

        }

    }
    @TypeConverter
    fun toString(value: List<String>?): String? = Gson().toJson(value)


}