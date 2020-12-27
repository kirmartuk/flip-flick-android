package com.martyuk.flipflick.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HostDeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val macAddress: String
)