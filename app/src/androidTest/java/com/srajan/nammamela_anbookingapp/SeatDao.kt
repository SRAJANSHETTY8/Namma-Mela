package com.srajan.nammamela_anbookingapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SeatDao {

    @Insert
    fun bookSeat(seat: Seat)

    @Query("SELECT * FROM seats")
    fun getAllSeats(): List<Seat>
}