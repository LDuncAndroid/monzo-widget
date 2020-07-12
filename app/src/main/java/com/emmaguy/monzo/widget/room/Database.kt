package com.emmaguy.monzo.widget.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DbPot::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun potsDao(): PotsDao
}