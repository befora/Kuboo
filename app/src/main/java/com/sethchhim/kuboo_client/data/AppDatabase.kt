package com.sethchhim.kuboo_client.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.data.model.Download
import com.sethchhim.kuboo_client.data.model.Favorite
import com.sethchhim.kuboo_client.data.model.Log
import com.sethchhim.kuboo_client.data.model.Recent
import com.sethchhim.kuboo_remote.model.Login

@Database(entities = [(Login::class), (Recent::class), (Favorite::class), (Download::class), (Log::class)], version = Constants.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDatabaseDao(): AppDatabaseDao

}