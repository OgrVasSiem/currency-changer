package com.module.testapp.data.network

import com.module.testapp.data.network.dto.LatestRatesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CurrencyApi {
    @GET("daily_json.js")
    fun getLatest(): Single<LatestRatesResponse>
}