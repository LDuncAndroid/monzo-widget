package com.emmaguy.monzo.widget.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface PotsDao {
    @Query("SELECT * FROM pots")
    fun pots(): Flowable<List<DbPot>>

    @Insert
    fun insert(pot: DbPot)
}