package com.example.wealthbuilder.data.repository

import com.example.wealthbuilder.data.csv.CSVParser
import com.example.wealthbuilder.data.local.StockDatabase
import com.example.wealthbuilder.data.mapper.toCompanyListing
import com.example.wealthbuilder.data.mapper.toCompanyListingEntity
import com.example.wealthbuilder.data.remote.StockApi
import com.example.wealthbuilder.domain.model.CompanyListing
import com.example.wealthbuilder.domain.repository.StockRepository
import com.example.wealthbuilder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRepository: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListings(query)
            emit(Resource.Success(localListings.map {
                it.toCompanyListing()
            }))
            val isDbEmpty = (localListings.isEmpty() && query.isBlank())
            val shouldJustFetchFromCache = !isDbEmpty && !fetchFromRepository
            if (shouldJustFetchFromCache) {
                emit(Resource.Loading(true))
                return@flow  // Done here!
            }
            val response = api.getListings()
            //val remoteListings = companyListingsParser.parse(response.byteStream())

            var remoteListings: List<CompanyListing>? = null
            try {
                remoteListings = companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Fetched NO data from CSV stream!"))
            } catch(e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Fetched NO data from CSV stream!"))
            }

            /*
            val remoteListings: List<CompanyListing> = try {
                companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Fetched NO data from CSV stream!"))
            } catch(e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Fetched NO data from CSV stream!"))
            }
            */

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map {
                        it.toCompanyListingEntity()
                    }
                )
            }
        }
    }
}