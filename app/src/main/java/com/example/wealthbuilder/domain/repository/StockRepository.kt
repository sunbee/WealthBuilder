package com.example.wealthbuilder.domain.repository

import com.example.wealthbuilder.domain.model.CompanyInfo
import com.example.wealthbuilder.domain.model.CompanyListing
import com.example.wealthbuilder.domain.model.IntradayInfo
import com.example.wealthbuilder.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}