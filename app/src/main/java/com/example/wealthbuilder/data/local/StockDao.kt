package com.example.wealthbuilder.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDao {

    /* (C)RUD */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyListings(companyListingEntities: List<CompanyListingEntity>)

    /* CRU(D) */
    @Query("DELETE FROM CompanyListingEntity")
    fun clearCompanyListings()

    /* C(R)UD */
    @Query(
        """
            SELECT *
            FROM CompanyListingEntity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
    fun searchCompanyListings(query: String): List<CompanyListingEntity>
}