package com.example.wealthbuilder.data.mapper

import com.example.wealthbuilder.data.dto.CompanyInfoDto
import com.example.wealthbuilder.data.local.CompanyListingEntity
import com.example.wealthbuilder.domain.model.CompanyInfo
import com.example.wealthbuilder.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        name = name ?: "",
        description = description ?: "",
        symbol = symbol ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}