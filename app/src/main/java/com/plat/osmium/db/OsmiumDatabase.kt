package com.plat.osmium.db

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [Directory::class], version = 1)
abstract class OsmiumDatabase : RoomDatabase() {
    abstract fun directoryDao(): DirectoryDao

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