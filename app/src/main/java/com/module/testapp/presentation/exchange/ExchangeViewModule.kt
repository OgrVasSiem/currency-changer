package com.module.testapp.presentation.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.module.testapp.data.local.dao.BalanceDao
import com.module.testapp.data.network.CurrencyApi
import com.module.testapp.presentation.exchange.model.CurrencyRate
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val balanceDao: BalanceDao
) : ViewModel() {
    private val _rates = MutableStateFlow<List<CurrencyRate>>(emptyList())
    val rates: StateFlow<List<CurrencyRate>> = _rates.asStateFlow()

    private val _balances = MutableStateFlow<Map<String, Double>>(emptyMap())
    val balances: StateFlow<Map<String, Double>> = _balances.asStateFlow()

    private val _effect = Channel<ExchangeContract.ViewEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val disposables = CompositeDisposable()

    init {
        fetchLatestRates()
        observeBalancesFromDb()
    }

    private fun fetchLatestRates() {
        currencyApi.getLatest()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                response.valute.values.map { dto ->
                    CurrencyRate(
                        code = dto.charCode,
                        name = dto.name,
                        nominal = dto.nominal,
                        value = dto.value,
                        previous = dto.previous
                    )
                }
            }
            .subscribeBy(
                onSuccess = { list ->
                    _rates.value = list
                },
                onError = { e ->
                    viewModelScope.launch {
                        _effect.send(
                            ExchangeContract.ViewEffect.ShowError(
                                e.message ?: ""
                            )
                        )
                    }
                }
            )
            .also { disposables.add(it) }
    }

    private fun observeBalancesFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            balanceDao.observeAll().collect { entities ->
                _balances.value = entities.associate { it.currencyCode to it.amount }
            }
        }
    }

    fun performExchange(
        fromCode: String,
        toCode: String,
        amount: Double,
        rate: Double
    ) {
        viewModelScope.launch {
            try {
                balanceDao.exchange(fromCode, toCode, amount, rate)
                _effect.send(ExchangeContract.ViewEffect.ShowSuccess)
            } catch (e: IllegalArgumentException) {
                _effect.send(
                    ExchangeContract.ViewEffect.ShowError(
                        e.message ?: ""
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
