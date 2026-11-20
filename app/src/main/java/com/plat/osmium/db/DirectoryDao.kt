package com.plat.osmium.db

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDao {
    @Query("SELECT * FROM directory")
    fun getAll(): Flow<List<Directory>>

    @Query("SELECT * FROM directory")
    suspend fun getAllNow(): List<Directory>

    @Query("SELECT * FROM directory WHERE id = :dirId")
    fun byId(dirId: Int): Flow<Directory?>

    @Update
    suspend fun update(directory: Directory)

    @Insert
    suspend fun insert(directory: Directory)

    @Delete
    suspend fun delete(directory: Directory)

}