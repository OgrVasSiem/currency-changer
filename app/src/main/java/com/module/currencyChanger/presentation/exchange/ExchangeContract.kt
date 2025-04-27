package com.module.currencyChanger.presentation.exchange

interface ExchangeContract {
    sealed class ViewEffect {
        data class ShowError(val message: String): ViewEffect()
        data object ShowSuccess: ViewEffect()
    }
}
