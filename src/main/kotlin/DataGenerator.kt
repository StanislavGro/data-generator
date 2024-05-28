package ru.one.files

import org.json.JSONObject
import ru.one.files.Constants.FOLDER_NAME
import ru.one.files.Constants.GENERATOR_COUNT
import ru.one.files.Constants.SEPARATOR
import ru.one.files.Constants.TEMPLATE_DATA_FILE_NAME
import ru.one.files.Constants.defaultIntRange
import ru.one.files.Constants.defaultLongRange
import ru.one.files.Constants.defaultStrRange
import ru.one.files.Constants.formatter
import ru.one.files.Constants.zoneId
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import kotlin.random.Random

fun main(args: Array<String>) {

//    val timeList = mutableListOf<Double>()

//    for (i in 1..10000) {

    val startTime = Instant.now().toEpochMilli()

    val inputFile = File(TEMPLATE_DATA_FILE_NAME)

    val jsonString = inputFile.inputStream().readAndClose()
    val jsonObject = JSONObject(jsonString)

    val jsonMap = jsonObject.toMap()
        .mapValues {
            it.value.toString()
        }

    val generatorRange = IntRange(0, GENERATOR_COUNT - 1)

    val outputFileName = outputFileName()
    val outputFile = File("$FOLDER_NAME/$outputFileName")

    outputFile.createNewFile()

    fillOutputFile(outputFile, generatorRange, jsonMap)

    println("Time: ${((Instant.now().toEpochMilli() - startTime).toDouble() / 1000)} c")

//        timeList.add((Instant.now().toEpochMilli() - startTime).toDouble() / 1000)
//    }

//    var timeSum: Double = 0.0
//    timeList.forEach { timeSum += it }
//    val avgTime = timeSum / timeList.size
//    println("Avg Time: $avgTime")
}

private fun outputFileName(): String {

    val prefix = "generated-data"

    val localDateTime = LocalDateTime.ofInstant(Instant.now(), zoneId)
    val middlePart = localDateTime.format(formatter)

    val extension = ".csv"

    return "${prefix}_$middlePart$extension"
}

private fun fillOutputFile(
    outputFile: File,
    generatorRange: IntRange,
    jsonMap: Map<String, String>
) {

    val columnRecordMap = mutableMapOf<String, List<String>>()

    try {

        val columns = jsonMap.keys

        columns.forEach { column ->

            val typeRule = jsonMap.getValue(column)

            val generatedValues = generateRecordsForColumn(typeRule)

            columnRecordMap[column] = generatedValues
        }
    } catch (e: Exception) {
        throw RuntimeException("Exception while generating data for column", e)
    }

    val transpositionMap = mutableListOf<MutableList<String>>()

    transpositionMap.add(columnRecordMap.keys.toMutableList())

    for (i in generatorRange) {

        val recordList = mutableListOf<String>()

        for (key in columnRecordMap.keys) {
            recordList.add(columnRecordMap.getValue(key)[i])
        }

        transpositionMap.add(recordList)
    }

    for (record in transpositionMap) {
        outputFile.appendText(record.joinToString(separator = SEPARATOR, postfix = "\n"))
    }

    println("File \"${outputFile.name}\" was successfully generated")
}

fun generateRecordsForColumn(dataTypeRule: String): List<String> {

    val trimDataTypeRule = dataTypeRule.trim()

    var range: String? = null

    val oneValue = if (trimDataTypeRule.contains("=")) {
        trimDataTypeRule.substringAfter("=")
    } else null

    var dataType =
        if (trimDataTypeRule.contains("(") && trimDataTypeRule.contains(")")) {
            range = trimDataTypeRule.substringAfter("(").substringBefore(")")
            trimDataTypeRule.replace("($range)", "")
                .substringBefore(" ")
        } else {
            trimDataTypeRule.substringBefore(" ")
        }

    if (oneValue != null) {
        dataType = dataType.replace("=$oneValue", "")
    }

    val typeRule = dataTypeRule.substringAfter(dataType).trim()
    val unique = typeRule.contains("unique", true)

    return when (dataType) {

        "integer" -> {
            generateIntegerByRule(
                integerLen = generateIntegerRange(range),
                unique = unique,
            )
        }

        "text" -> {
            generateTextByRule(
                textLen = generateStringRange(range),
                unique = unique,
                oneValue = oneValue
            )
        }

        "varchar" -> {
            generateTextByRule(
                textLen = generateStringRange(range),
                unique = unique,
                oneValue = oneValue,
            )
        }

        "boolean" -> {
            generateBooleanByRule()
        }

        "smallint" -> {
            generateSmallintByRule(
                smallintLen = generateIntegerRange(range),
                unique = unique,
            )
        }

        "bigint" -> {
            generateBigintByRule(
                bigintLen = generateBigintRange(range),
                unique = unique,
                oneValue = oneValue?.toLong(),
            )
        }

        else -> throw RuntimeException("Data type $dataType is not supported!")
    }
}

private fun generateIntegerRange(integerLen: String?): IntRange =
    integerLen?.let {

        val numbers = integerLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> IntRange(1, numbers.last().toInt())
            2 -> IntRange(numbers.first().toInt(), numbers.last().toInt())
            else -> throw Exception("Incorrect range")
        }
    } ?: defaultIntRange

private fun generateStringRange(strLen: String?): IntRange =
    strLen?.let {

        val numbers = strLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> IntRange(1, numbers.last().toInt())
            2 -> IntRange(numbers.first().toInt(), numbers.last().toInt())
            else -> throw Exception("Incorrect range")
        }
    } ?: defaultStrRange

private fun generateBigintRange(bigintLen: String?): LongRange =
    bigintLen?.let {

        val numbers = bigintLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> LongRange(1, numbers.last().toLong())
            2 -> LongRange(numbers.first().toLong(), numbers.last().toLong())
            else -> throw Exception("Incorrect range")
        }
    } ?: defaultLongRange

private fun generateSmallintByRule(
    smallintLen: IntRange = IntRange(1, Short.MAX_VALUE.toInt()),
    unique: Boolean = false,
): List<String> {

    return if (unique) {
        val smallintSet = mutableListOf<String>()

        for (i in 0 until GENERATOR_COUNT) {
            smallintSet.add(generateInteger(smallintLen).toString())
        }

        smallintSet.toList()
    } else {
        val smallintList = mutableListOf<String>()

        for (i in 0 until GENERATOR_COUNT) {
            smallintList.add(generateInteger(smallintLen).toString())
        }

        smallintList
    }
}

private fun generateIntegerByRule(
    integerLen: IntRange = defaultIntRange,
    unique: Boolean = false,
): List<String> {

    return if (unique) {

        val intSet = mutableSetOf<String>()

        for (i in 0 until GENERATOR_COUNT) {
            intSet.add(generateInteger(integerLen).toString())
        }

        intSet.toList()
    } else {

        val listInt = mutableListOf<String>()

        for (i in 0 until GENERATOR_COUNT) {
            listInt.add(generateInteger(integerLen).toString())
        }

        listInt
    }
}

private fun generateInteger(integerLen: IntRange): Int =
    Random.nextInt(integerLen.first, integerLen.last)

private fun generateBigintByRule(
    bigintLen: LongRange = defaultLongRange,
    unique: Boolean = false,
    oneValue: Long?,
): List<String> {

    return if (oneValue != null) {
        val bigintList = mutableListOf<String>()

        for (i in 0 until GENERATOR_COUNT) {
            bigintList.add(oneValue.toString())
        }

        bigintList
    } else {
        if (unique) {

            val bigintSet = mutableSetOf<String>()

            while (bigintSet.size < GENERATOR_COUNT) {
                bigintSet.add(generateBigint(bigintLen).toString())
            }

            bigintSet.toList()
        } else {

            val bigintList = mutableListOf<String>()

            for (i in 0 until GENERATOR_COUNT) {
                bigintList.add(generateBigint(bigintLen).toString())
            }

            bigintList
        }
    }
}

private fun generateBigint(bigintLen: LongRange): Long =
    Random.nextLong(bigintLen.first, bigintLen.last)


private fun generateBooleanByRule(): List<String> {

    val listBoolean = mutableListOf<String>()

    for (i in 0 until GENERATOR_COUNT) {
        listBoolean.add(
            Random.nextBoolean().toString()
        )
    }

    return listBoolean
}

private fun generateTextByRule(
    textLen: IntRange = defaultStrRange,
    unique: Boolean,
    oneValue: String?,
): List<String> {

    return if (oneValue != null) {
        val textList = mutableListOf<String>()

        for (i in 0 until GENERATOR_COUNT) {
            textList.add(oneValue)
        }

        textList
    } else {
        if (unique) {

            val testSet = mutableSetOf<String>()

            while (testSet.size < GENERATOR_COUNT) {
                testSet.add(generateText(textLen))
            }

            testSet.toList()

        } else {

            val textList = mutableListOf<String>()

            for (i in 0 until GENERATOR_COUNT) {
                textList.add(generateText(textLen))
            }

            textList
        }
    }
}

private fun generateText(textLen: IntRange): String {

    val stringLen = Random.nextInt(textLen.first, textLen.last)
    val text = StringBuilder()

    for (j in 0..stringLen) {
        text.append(Random.nextInt(65, 122).toChar())
    }

    return text.toString()
}

private fun Int.pow(degree: Int): Int {

    if (degree == 0)
        return 1
    else if (degree == 1)
        return this

    var result = this
    var start = 2

    while (start <= degree) {
        result *= this
        start += 1
    }

    return result
}
