package com.emmaguy.monzo.widget.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface PotsDao {
    @Query("SELECT * FROM pots")
    fun pots(): Flowable<List<PotEntity>>

    @Insert
    fun insert(pot: PotEntity)
}