package com.srajan.nammamela_anbookingapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seats")
data class Seat(

    @PrimaryKey
    val seatNumber: String

)