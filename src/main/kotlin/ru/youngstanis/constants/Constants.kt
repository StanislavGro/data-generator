package ru.youngstanis.constants

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Constants {

    // Time constants
    val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd-LLL-yyyy_HH:mm:ss", Locale.ENGLISH)

    val zoneId: ZoneId = ZoneId.of("UTC")

    // Range constants
    val defaultIntRange = IntRange(1, Int.MAX_VALUE)
    val defaultShortRange = IntRange(1, Short.MAX_VALUE.toInt())
    val defaultStrRange = IntRange(1, 10)
    val defaultLongRange = LongRange(1, Long.MAX_VALUE)

}