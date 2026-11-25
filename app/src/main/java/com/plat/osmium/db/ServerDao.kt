package com.plat.osmium.db

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

  @Query("SELECT * FROM server")
  fun getAll(): Flow<List<Server>>

  @Query("SELECT * FROM server")
  suspend fun getAllNow(): List<Server>

  @Query("SELECT * FROM server WHERE id = :serverId")
  suspend fun byId(serverId: Int): Server?

  @Query("SELECT * FROM server WHERE id = :serverId")
   fun flowById(serverId: Int): Flow<Server?>

  @Update
  suspend fun update(server: Server)

  @Insert
  suspend fun insert(server: Server)

  @Delete
  suspend fun delete(server: Server)
}
