package com.github.gunin_igor75.stock_report.di

import com.github.gunin_igor75.stock_report.data.network.ApiFactory
import com.github.gunin_igor75.stock_report.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {


    companion object{

        @Provides
        @Singleton
        fun providesApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}