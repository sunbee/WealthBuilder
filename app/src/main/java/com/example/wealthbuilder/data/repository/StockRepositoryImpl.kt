package com.example.wealthbuilder.data.repository

import android.util.Log
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
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        Log.d("DEBUG_DATA", "getCompanyListings says Hi!")
        return flow {
            emit(Resource.Loading(true))
            Log.d("DEBUG_DATA", "Emitted resource type loading from Stock Repo!")
            val localListings = dao.searchCompanyListings(query)
            emit(Resource.Success(
                data = localListings.map {
                    it.toCompanyListing()
                })
            )
            Log.d("DEBUG_DATA", "Emitted resource type success from Stock Repo!")
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustFetchFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustFetchFromCache) {
                emit(Resource.Loading(false))
                Log.d("DEBUG_DATA", "shouldJustFetchFromCache is ${shouldJustFetchFromCache}")
                return@flow  // Done here!
            }
            Log.d("DEBUG_DATA", "shouldJustFetchFromCache is ${shouldJustFetchFromCache}")

            val remoteListings = try {
                Log.d("DEBUG_DATA", "Entered try-catch block in Stock Repo!")
                val response = api.getListings()
                Log.d("DEBUG_DATA", "Got ${response.contentLength()} from api in Stock Repo")
                companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Fetched NO data from CSV stream!"))
                null
            } catch(e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Fetched NO data from CSV stream!"))
                null
            }

            Log.d("DEBUG_DATA", "Got past try-catch in Stock Repo!")
            remoteListings?.let { listings ->
                Log.d("DEBUG_DATA", "Got listings in Stock Repo with size ${listings.size}")
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map {
                        it.toCompanyListingEntity()
                    }
                )
                emit(Resource.Success(
                    data=dao
                        .searchCompanyListings(query="")
                        .map {
                            it.toCompanyListing()
                        }))
                emit(Resource.Loading(isLoading = false))
            }  // end LISTINGS
        }  // end FLOW
    }  // end FUN
}  // end IMPL
