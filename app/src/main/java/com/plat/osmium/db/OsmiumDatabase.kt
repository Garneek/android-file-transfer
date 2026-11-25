package com.plat.osmium.db

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase


// Remember
@Database(entities = [Directory::class, Server::class], version = 2)
@ColumnTypeConverters(Converters::class)
abstract class OsmiumDatabase : RoomDatabase() {
    abstract fun directoryDao(): DirectoryDao
    abstract fun serverDao(): ServerDao

    companion object {
        @Volatile
        private var INSTANCE: OsmiumDatabase? = null

        fun getDatabase(context: Context): OsmiumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        OsmiumDatabase::class.java,
                        "osmium_database"
                    ).build()
                INSTANCE = instance
                instance
            }
        }
   }
}