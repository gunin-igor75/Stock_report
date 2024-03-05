package com.github.gunin_igor75.stock_report.di

import com.github.gunin_igor75.stock_report.data.repository.BarRepositoryImp
import com.github.gunin_igor75.stock_report.domain.repository.BarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Singleton
    @Binds
    fun bindsParRepository(imp: BarRepositoryImp): BarRepository
}