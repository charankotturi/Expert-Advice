package com.sales.dashboard.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USER")
data class Profile (

    @PrimaryKey(autoGenerate = false)
    var id: Int? = 1,

    var phoneNumber: Int,

    var password: String?,

    var profileImage: String?,

    var admin: Boolean,
)