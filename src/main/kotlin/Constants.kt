package ru.one.files

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Constants {

    // Time constants
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-LLL-yyyy_HH:mm:ss", Locale.ENGLISH)
    val zoneId: ZoneId = ZoneId.of("UTC")

    // Range constants
    val defaultIntRange = IntRange(1, Int.MAX_VALUE)
    val defaultStrRange = IntRange(1, 10)
    val defaultLongRange = LongRange(1, Long.MAX_VALUE)

    // File constants
    const val TEMPLATE_DATA_FILE_NAME = "rows.json"
    const val FOLDER_NAME = "test-data"
//    const val GENERATOR_COUNT = 500000
    const val SEPARATOR = ","

}