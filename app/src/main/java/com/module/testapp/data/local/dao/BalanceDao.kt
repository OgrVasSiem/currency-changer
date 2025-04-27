package com.module.testapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.module.testapp.data.local.entity.BalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Query("SELECT * FROM balances")
    fun observeAll(): Flow<List<BalanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BalanceEntity)

    @Query("SELECT amount FROM balances WHERE currencyCode = :code")
    suspend fun getAmount(code: String): Double?

    @Update
    suspend fun update(entity: BalanceEntity)

    @Delete
    suspend fun delete(entity: BalanceEntity)

    @Transaction
    suspend fun exchange(
        fromCode: String,
        toCode: String,
        amountFrom: Double,
        rate: Double
    ) {
        val fromBal = getAmount(fromCode) ?: 0.0
        val toBal = getAmount(toCode) ?: 0.0

        if (fromBal < amountFrom) {
            throw IllegalArgumentException("Insufficient funds: have $fromBal, need $amountFrom")
        }

        val newFrom = fromBal - amountFrom
        val newTo = toBal + amountFrom * rate

        upsert(BalanceEntity(currencyCode = fromCode, amount = newFrom))
        upsert(BalanceEntity(currencyCode = toCode, amount = newTo))
    }
}