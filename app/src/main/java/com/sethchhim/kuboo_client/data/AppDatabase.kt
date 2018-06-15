package com.sethchhim.kuboo_client.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.data.model.Download
import com.sethchhim.kuboo_client.data.model.Favorite
import com.sethchhim.kuboo_client.data.model.Recent
import com.sethchhim.kuboo_remote.model.Login

@Database(entities = [(Login::class), (Recent::class), (Favorite::class), (Download::class)], version = Constants.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDatabaseDao(): AppDatabaseDao

}