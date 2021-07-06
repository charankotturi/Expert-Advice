package com.sales.dashboard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Profile::class], version = 3, exportSchema = false)
abstract class ProfileDataBase : RoomDatabase(){

    abstract fun profileDao() : ProfileDao
}