package com.module.testapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.module.testapp.R
import com.module.testapp.presentation.exchange.ExchangeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TestApp)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ExchangeFragment())
                .commit()
        }
    }
}