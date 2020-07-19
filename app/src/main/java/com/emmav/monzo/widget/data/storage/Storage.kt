package com.emmav.monzo.widget.data.storage

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface Storage {

    @Transaction
    @Query("SELECT * FROM accounts")
    fun accountsWithBalance(): Observable<List<DbAccountWithBalance>>

    @Query("SELECT * FROM accounts")
    fun accounts(): Observable<List<DbAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAccounts(accounts: List<DbAccount>)

    @Query("SELECT * FROM balance")
    fun balance(): Observable<List<DbBalance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveBalance(dbBalance: DbBalance)

    @Query("SELECT * FROM pots")
    fun pots(): Observable<List<DbPot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePots(pots: List<DbPot>)

    @Transaction
    @Query("SELECT * FROM widgets WHERE id = :id")
    fun widgetById(id: Int): Single<List<DbWidgetWithRelations>>

    @Transaction
    @Query("SELECT * FROM widgets")
    fun widgets(): Observable<List<DbWidgetWithRelations>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveWidget(dbWidget: DbWidget)

    @Query("DELETE FROM widgets WHERE id NOT IN (:widgetIds)")
    fun deleteAllWidgetsExcept(widgetIds: List<Int>)
}