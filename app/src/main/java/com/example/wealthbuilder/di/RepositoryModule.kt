package com.example.wealthbuilder.di

import com.example.wealthbuilder.data.csv.CSVParser
import com.example.wealthbuilder.data.csv.CompanyListingsParser
import com.example.wealthbuilder.data.repository.StockRepositoryImpl
import com.example.wealthbuilder.domain.model.CompanyListing
import com.example.wealthbuilder.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}



