package com.emmaguy.monzo.widget.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    DbAccount::class,
    DbBalance::class,
    DbPot::class,
    DbWidget::class
], version = 4, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun storage(): Storage
}