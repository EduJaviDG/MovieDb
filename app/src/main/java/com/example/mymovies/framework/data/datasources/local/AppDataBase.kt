package com.example.mymovies.framework.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mymovies.data.datasources.local.MovieDb
import com.example.mymovies.util.Converters

@Database(entities = [MovieDb::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
abstract fun movieDao(): MovieDao

}