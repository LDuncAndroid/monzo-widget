package com.emmav.monzo.widget.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DbAccount::class,
        DbBalance::class,
        DbPot::class,
        DbWidget::class
    ], version = 4, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storage(): MonzoStorage
}