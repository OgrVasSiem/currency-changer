package com.module.currencyChanger.presentation.exchange.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.module.currencyChanger.R
import com.module.currencyChanger.presentation.exchange.model.CurrencyRate

class CurrencyOutputAdapter(
    private var items: List<CurrencyRate>,
    private val balances: () -> Map<String, Double>,
    private val enteredMap: MutableMap<Int, Double>
) : RecyclerView.Adapter<CurrencyOutputAdapter.VH>() {

    var baseIndex: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(new: List<CurrencyRate>) {
        items = new
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_currency_output, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCode = view.findViewById<TextView>(R.id.tv_currency_code)
        private val tvAmount = view.findViewById<TextView>(R.id.tv_amount)
        private val tvBalance = view.findViewById<TextView>(R.id.tv_balance)

        fun bind(rate: CurrencyRate) {
            tvCode.text = rate.code

            val balance = balances()[rate.code] ?: 0.0
            tvBalance.text = itemView.context.getString(R.string.balance_template, balance, rate.code)

            val fromRate = items.getOrNull(baseIndex) ?: return
            val fromAmount = enteredMap[baseIndex] ?: 0.0
            val unitFrom = fromRate.value / fromRate.nominal
            val unitTo = rate.value / rate.nominal
            val result = if (unitTo != 0.0) fromAmount * (unitFrom / unitTo) else 0.0

            tvAmount.text = "%.2f".format(result)
        }
    }
}
