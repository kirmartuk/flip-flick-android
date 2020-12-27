package com.martyuk.flipflick.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.martyuk.flipflick.entities.HostDeviceEntity

@Database(entities = [HostDeviceEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hostDeviceEntityDao(): HostDeviceEntityDao

    companion object {
        var INSTANCE: AppDatabase? = null
        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "flickflip.db"
                    ).build()
                }
            }
            return INSTANCE
        }


        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}