package com.module.testapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.module.testapp.data.local.dao.BalanceDao
import com.module.testapp.data.local.entity.BalanceEntity

@Database(
    entities = [BalanceEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
}