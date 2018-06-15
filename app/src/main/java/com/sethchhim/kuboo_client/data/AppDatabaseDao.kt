package com.sethchhim.kuboo_client.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sethchhim.kuboo_client.data.model.Download
import com.sethchhim.kuboo_client.data.model.Favorite
import com.sethchhim.kuboo_client.data.model.Recent
import com.sethchhim.kuboo_remote.model.Login

@Dao
interface AppDatabaseDao {

    //Login
    @Query("select * from Login")
    fun getAllLogins(): LiveData<List<Login>>

    @Query("select * from Login where id = :id")
    fun findLoginsById(id: Long): LiveData<Login>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogin(loginItem: Login)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLogin(loginItem: Login)

    @Delete
    fun deleteLogin(loginItem: Login)

    //Recent
    @Query("select * from Recent")
    fun getAllBookRecent(): List<Recent>

    @Query("select * from Recent where id = :id")
    fun findRecentById(id: Int): Recent

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecent(recent: Recent)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRecent(recent: Recent)

    @Delete
    fun deleteRecent(recent: Recent)

    @Query("DELETE FROM Recent")
    fun deleteRecentAll()

    //Favorite
    @Query("select * from Favorite")
    fun getAllBookFavorite(): List<Favorite>

    @Query("select * from Favorite where id = :id")
    fun findFavoriteById(id: Long): Favorite

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: Favorite)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavorite(favorite: Favorite)

    @Delete
    fun deleteFavorite(favorite: Favorite)

    @Query("DELETE FROM Favorite")
    fun deleteFavoriteAll()

    //Download
    @Query("select * from Download")
    fun getAllBookDownload(): List<Download>

    @Query("select * from Download where id = :id")
    fun findDownloadById(id: Long): Download

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownload(download: Download)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDownload(download: Download)

    @Delete
    fun deleteDownload(download: Download)

    @Query("DELETE FROM Download")
    fun deleteDownloadAll()

}