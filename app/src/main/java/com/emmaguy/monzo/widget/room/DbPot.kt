package com.emmaguy.monzo.widget.room

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pots")
data class DbPot(
        @PrimaryKey val id: String,
        val name: String,
        val balance: Long,
        val currency: String) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DbPot>() {
            override fun areItemsTheSame(oldItem: DbPot, newItem: DbPot): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DbPot, newItem: DbPot): Boolean {
                return oldItem == newItem
            }
        }
    }
}