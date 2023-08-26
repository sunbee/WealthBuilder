package com.example.wealthbuilder.domain.model

import java.time.LocalDateTime

data class IntradayInfo(
    val timeStamp: LocalDateTime,
    val close: Double
)
