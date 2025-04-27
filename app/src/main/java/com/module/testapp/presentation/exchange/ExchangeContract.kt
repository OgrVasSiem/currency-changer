package com.module.testapp.presentation.exchange

interface ExchangeContract {
    sealed class ViewEffect {
        data class ShowError(val message: String): ViewEffect()
        data object ShowSuccess: ViewEffect()
    }
}
