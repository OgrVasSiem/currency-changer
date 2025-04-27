package com.module.currencyChanger.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.module.currencyChanger.R
import com.module.currencyChanger.presentation.exchange.ExchangeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_CurrencyChanger)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ExchangeFragment())
                .commit()
        }
    }
}