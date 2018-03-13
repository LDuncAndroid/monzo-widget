package com.emmaguy.monzo.widget.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "pots")
data class PotEntity(
        @PrimaryKey val id: String,
        val name: String,
        val balance: Long,
        val currency: String)