package com.module.testapp.presentation.exchange.model

data class CurrencyRate(
    val code: String,
    val name: String,
    val nominal: Int,
    val value: Double,
    val previous: Double
)