package com.module.currencyChanger.presentation.exchange

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.module.currencyChanger.R
import com.module.currencyChanger.presentation.exchange.adapter.CurrencyInputAdapter
import com.module.currencyChanger.presentation.exchange.adapter.CurrencyOutputAdapter
import com.module.currencyChanger.presentation.exchange.model.CurrencyRate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExchangeFragment : Fragment(R.layout.fragment_exchange) {

    private val vm: ExchangeViewModel by viewModels()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var vpFrom: ViewPager2
    private lateinit var vpTo: ViewPager2
    private lateinit var arrow: ImageView

    private lateinit var fromAdapter: CurrencyInputAdapter
    private lateinit var toAdapter: CurrencyOutputAdapter

    private var baseIndex = 0
    private var latest: List<CurrencyRate> = emptyList()
    private val entered = mutableMapOf<Int, Double>()

    companion object {
        private val ALLOWED_CODES = listOf("USD", "EUR", "SGD")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupListeners()
        observeViewModel()
    }

    private fun setupViews(view: View) {
        toolbar = view.findViewById(R.id.topAppBar)
        vpFrom = view.findViewById(R.id.vpFrom)
        vpTo = view.findViewById(R.id.vpTo)
        arrow = view.findViewById(R.id.iv_arrow)

        fromAdapter = CurrencyInputAdapter(
            items = latest,
            balances = { vm.balances.value },
            enteredMap = entered
        ) { pos, amt ->
            entered[pos] = amt
            toAdapter.notifyDataSetChanged()
        }

        toAdapter = CurrencyOutputAdapter(
            items = latest,
            balances = { vm.balances.value },
            enteredMap = entered
        ).apply {
            baseIndex = 0
        }

        vpFrom.adapter = fromAdapter
        vpTo.adapter = toAdapter
    }

    private fun setupListeners() {
        vpFrom.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                baseIndex = position
                toAdapter.baseIndex = position
                toAdapter.notifyDataSetChanged()
            }
        })

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_exchange) {
                performExchange()
                true
            } else false
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.rates.collectLatest { allRates ->
                latest = ALLOWED_CODES.mapNotNull { code ->
                    allRates.firstOrNull { it.code == code }
                }
                if (latest.size == ALLOWED_CODES.size) {
                    fromAdapter.submitList(latest)
                    toAdapter.submitList(latest)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.effect.collectLatest { effect ->
                handleEffect(effect)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.balances.collectLatest {
                fromAdapter.notifyDataSetChanged()
                toAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun handleEffect(effect: ExchangeContract.ViewEffect) {
        when (effect) {
            is ExchangeContract.ViewEffect.ShowSuccess -> {
                showDialog(
                    title = getString(R.string.exchange_success_title),
                    message = getString(R.string.exchange_success_message)
                )
            }
            is ExchangeContract.ViewEffect.ShowError -> {
                showDialog(
                    title = getString(R.string.exchange_error_title),
                    message = getString(R.string.exchange_error_message_generic)
                )
            }
        }
    }

    private fun performExchange() {
        val fromPos = vpFrom.currentItem
        val toPos = vpTo.currentItem
        val fromRate = latest.getOrNull(fromPos) ?: return
        val toRate = latest.getOrNull(toPos) ?: return

        if (fromRate.code == toRate.code) {
            showDialog(
                title = getString(R.string.exchange_error_title),
                message = getString(R.string.exchange_same_currency_error)
            )
            return
        }

        val amount = entered[fromPos] ?: 0.0
        val unitFrom = fromRate.value / fromRate.nominal
        val unitTo = toRate.value / toRate.nominal
        val rate = if (unitTo != 0.0) unitFrom / unitTo else 0.0

        vm.performExchange(
            fromCode = fromRate.code,
            toCode = toRate.code,
            amount = amount,
            rate = rate
        )

        entered.remove(fromPos)
        fromAdapter.notifyItemChanged(fromPos)
        toAdapter.notifyDataSetChanged()
    }



    private fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
}
