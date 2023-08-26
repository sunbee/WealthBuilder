package com.example.wealthbuilder.domain.model

import com.squareup.moshi.Json

data class CompanyInfo(
    val name: String,
    val description: String,
    val symbol: String,
    val country: String,
    val industry: String
)
