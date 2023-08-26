package com.example.wealthbuilder.data.csv

import com.example.wealthbuilder.data.dto.IntradayInfoDto
import com.example.wealthbuilder.data.mapper.toIntradayInfo
import com.example.wealthbuilder.domain.model.IntradayInfo
import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor(): CSVParser<IntradayInfo> {

    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return csvReader
            .readAll()
            .drop(n = 1)
            .mapNotNull { line ->
                val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                val close = line.getOrNull(4) ?: return@mapNotNull null
                val dto = IntradayInfoDto(timestamp, close.toDouble())
                dto.toIntradayInfo()
            }
            .filter {
                it.timeStamp.dayOfMonth == LocalDateTime.now().plusDays(-1).dayOfMonth
            }
            .sortedBy {
                it.timeStamp.hour
            }
            .also {
                csvReader.close()
            }
    }


}