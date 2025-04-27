package com.module.testapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balances")
data class BalanceEntity(
    @PrimaryKey val currencyCode: String,
    val amount: Double
)