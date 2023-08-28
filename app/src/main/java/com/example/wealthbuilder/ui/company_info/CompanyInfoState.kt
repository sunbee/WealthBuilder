package com.example.wealthbuilder.ui.company_info

import com.example.wealthbuilder.domain.model.CompanyInfo
import com.example.wealthbuilder.domain.model.IntradayInfo

data class CompanyInfoState(
    val company: CompanyInfo? = null,
    val stockTrades: List<IntradayInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
