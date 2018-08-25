package com.sethchhim.kuboo_client.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object AppDatabaseMigrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val string = "CREATE TABLE LOG (" +
                    "autoId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "currentTimeMilliseconds BIGINT NOT NULL, " +
                    "logType INTEGER NOT NULL DEFAULT 0, " +
                    "message TEXT NOT NULL, " +
                    "isError INTEGER NOT NULL);"
            database.execSQL(string)
        }
    }

}