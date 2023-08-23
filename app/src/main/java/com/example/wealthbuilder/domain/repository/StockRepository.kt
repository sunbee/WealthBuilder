package com.example.wealthbuilder.domain.repository

import com.example.wealthbuilder.domain.model.CompanyListing
import com.example.wealthbuilder.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}