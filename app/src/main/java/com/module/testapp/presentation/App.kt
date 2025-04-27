package com.module.testapp.presentation

import android.app.Application
import com.module.testapp.data.local.AppDatabase
import com.module.testapp.data.local.entity.BalanceEntity
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.balanceDao()
            listOf("USD","EUR","SGD").forEach { code ->
                if (dao.getAmount(code) == null) {
                    dao.upsert(BalanceEntity(
                        currencyCode = code,
                        amount = 100.0)
                    )
                }
            }
        }
    }
}
