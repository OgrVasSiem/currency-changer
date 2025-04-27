package com.module.currencyChanger.di

import android.content.Context
import androidx.room.Room
import com.module.currencyChanger.data.local.AppDatabase
import com.module.currencyChanger.data.local.dao.BalanceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "app-db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideBalanceDao(db: AppDatabase): BalanceDao =
        db.balanceDao()
}