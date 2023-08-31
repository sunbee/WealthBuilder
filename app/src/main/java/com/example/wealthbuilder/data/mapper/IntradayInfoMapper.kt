package com.example.wealthbuilder.data.mapper

import com.example.wealthbuilder.data.dto.IntradayInfoDto
import com.example.wealthbuilder.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        timeStamp = localDateTime,
        close = close
    )
}