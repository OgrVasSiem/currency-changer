package com.module.currencyChanger.presentation.exchange.adapter

import android.annotation.SuppressLint
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.module.currencyChanger.R
import com.module.currencyChanger.presentation.exchange.model.CurrencyRate

class CurrencyInputAdapter(
    private var items: List<CurrencyRate>,
    private val balances: () -> Map<String, Double>,
    private val enteredMap: MutableMap<Int, Double>,
    private val onAmountChanged: (position: Int, amount: Double) -> Unit
) : RecyclerView.Adapter<CurrencyInputAdapter.VH>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(new: List<CurrencyRate>) {
        items = new
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_currency_input, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position], position)

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCode = view.findViewById<TextView>(R.id.tv_currency_code)
        private val etAmount = view.findViewById<EditText>(R.id.et_amount)
        private val tvBalance = view.findViewById<TextView>(R.id.tv_balance)
        private var watcher: TextWatcher? = null

        fun bind(rate: CurrencyRate, position: Int) {
            tvCode.text = rate.code

            val balance = balances()[rate.code] ?: 0.0
            tvBalance.text = itemView.context.getString(R.string.balance_template, balance, rate.code)

            watcher?.let { etAmount.removeTextChangedListener(it) }
            val text = enteredMap[position]?.let { "%.2f".format(it) } ?: ""
            if (etAmount.text.toString() != text) etAmount.setText(text)

            watcher = etAmount.doOnTextChanged { text, _, _, _ ->
                val value = text?.toString()?.toDoubleOrNull()
                if (value != null) {
                    enteredMap[position] = value
                    onAmountChanged(position, value)
                } else {
                    enteredMap.remove(position)
                    onAmountChanged(position, 0.0)
                }
            }
        }
    }
}
