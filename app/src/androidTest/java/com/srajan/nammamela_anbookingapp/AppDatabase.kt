package com.srajan.nammamela_anbookingapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Seat::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun seatDao(): SeatDao
}