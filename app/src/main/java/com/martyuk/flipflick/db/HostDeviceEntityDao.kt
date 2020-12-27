package com.martyuk.flipflick.db

import androidx.room.*
import com.martyuk.flipflick.entities.HostDeviceEntity

@Dao
interface HostDeviceEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHostDeviceEntity(hostDeviceEntity: HostDeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHostDeviceEntities(hostDeviceEntities: List<HostDeviceEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateHostDeviceEntity(hostDeviceEntity: HostDeviceEntity)

    @Delete
    fun deleteHostDeviceEntity(hostDeviceEntity: HostDeviceEntity)

    @Query("SELECT * FROM HostDeviceEntity WHERE id == :id")
    fun getHostDeviceEntityById(id: Int): HostDeviceEntity

    @Query("SELECT * FROM HostDeviceEntity")
    fun getHostDeviceEntities(): List<HostDeviceEntity>
}