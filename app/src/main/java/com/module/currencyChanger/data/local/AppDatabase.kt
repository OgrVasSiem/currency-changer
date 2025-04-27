package com.module.currencyChanger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.module.currencyChanger.data.local.dao.BalanceDao
import com.module.currencyChanger.data.local.entity.BalanceEntity

@Database(
    entities = [BalanceEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
}