package com.emmaguy.monzo.widget.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [PotEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun potsDao(): PotsDao
}