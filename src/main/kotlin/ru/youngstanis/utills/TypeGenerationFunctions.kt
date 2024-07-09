package ru.youngstanis.utills

import ru.youngstanis.constants.Constants
import kotlin.random.Random

fun generateIntegerRange(integerLen: String?): IntRange =
    integerLen?.let {

        val numbers = integerLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> IntRange(1, numbers.last().toInt())
            2 -> IntRange(numbers.first().toInt(), numbers.last().toInt())
            else -> throw Exception("Incorrect range")
        }
    } ?: Constants.defaultIntRange

fun generateSmallintRange(smallintLen: String?): IntRange =
    smallintLen?.let {

        val numbers = smallintLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> IntRange(1, numbers.last().toInt())
            2 -> IntRange(numbers.first().toInt(), numbers.last().toInt())
            else -> throw Exception("Incorrect range")
        }
    } ?: Constants.defaultShortRange

fun generateStringRange(strLen: String?): IntRange =
    strLen?.let {

        val numbers = strLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> IntRange(1, numbers.last().toInt())
            2 -> IntRange(numbers.first().toInt(), numbers.last().toInt())
            else -> throw Exception("Incorrect range")
        }
    } ?: Constants.defaultStrRange

fun generateBigintRange(bigintLen: String?): LongRange =
    bigintLen?.let {

        val numbers = bigintLen.split(",")
            .map { it.trim() }

        when (numbers.size) {
            1 -> LongRange(1, numbers.last().toLong())
            2 -> LongRange(numbers.first().toLong(), numbers.last().toLong())
            else -> throw Exception("Incorrect range")
        }
    } ?: Constants.defaultLongRange

fun generateSmallintByRule(
    smallintLen: IntRange = IntRange(1, Short.MAX_VALUE.toInt()),
    unique: Boolean = false,
    generatorCount: Int,
): List<String> {

    return if (unique) {
        val smallintSet = mutableListOf<String>()

        for (i in 0 until generatorCount) {
            smallintSet.add(generateInteger(smallintLen).toString())
        }

        smallintSet.toList()
    } else {
        val smallintList = mutableListOf<String>()

        for (i in 0 until generatorCount) {
            smallintList.add(generateInteger(smallintLen).toString())
        }

        smallintList
    }
}

fun generateIntegerByRule(
    integerLen: IntRange = Constants.defaultIntRange,
    unique: Boolean = false,
    generatorCount: Int,
): List<String> {

    return if (unique) {

        val intSet = mutableSetOf<String>()

        for (i in 0 until generatorCount) {
            intSet.add(generateInteger(integerLen).toString())
        }

        intSet.toList()
    } else {

        val listInt = mutableListOf<String>()

        for (i in 0 until generatorCount) {
            listInt.add(generateInteger(integerLen).toString())
        }

        listInt
    }
}

fun generateBigintByRule(
    bigintLen: LongRange = Constants.defaultLongRange,
    unique: Boolean = false,
    oneValue: Long?,
    generatorCount: Int,
): List<String> {

    return if (oneValue != null) {
        val bigintList = mutableListOf<String>()

        for (i in 0 until generatorCount) {
            bigintList.add(oneValue.toString())
        }

        bigintList
    } else {
        if (unique) {

            val bigintSet = mutableSetOf<String>()

            while (bigintSet.size < generatorCount) {
                bigintSet.add(generateBigint(bigintLen).toString())
            }

            bigintSet.toList()
        } else {

            val bigintList = mutableListOf<String>()

            for (i in 0 until generatorCount) {
                bigintList.add(generateBigint(bigintLen).toString())
            }

            bigintList
        }
    }
}

fun generateTextByRule(
    textLen: IntRange = Constants.defaultStrRange,
    unique: Boolean,
    oneValue: String?,
    generatorCount: Int,
): List<String> {

    return if (oneValue != null) {
        val textList = mutableListOf<String>()

        for (i in 0 until generatorCount) {
            textList.add(oneValue)
        }

        textList
    } else {
        if (unique) {

            val testSet = mutableSetOf<String>()

            while (testSet.size < generatorCount) {
                testSet.add(generateText(textLen))
            }

            testSet.toList()

        } else {

            val textList = mutableListOf<String>()

            for (i in 0 until generatorCount) {
                textList.add(generateText(textLen))
            }

            textList
        }
    }
}

fun generateBooleanByRule(generatorCount: Int): List<String> {

    val listBoolean = mutableListOf<String>()

    for (i in 0 until generatorCount) {
        listBoolean.add(
            Random.nextBoolean().toString()
        )
    }

    return listBoolean
}

private fun generateBigint(bigintLen: LongRange): Long =
    Random.nextLong(bigintLen.first, bigintLen.last)

private fun generateInteger(integerLen: IntRange): Int =
    Random.nextInt(integerLen.first, integerLen.last)

private fun generateText(textLen: IntRange): String {

    val stringLen = Random.nextInt(textLen.first, textLen.last)
    val text = StringBuilder()

    for (j in 0..stringLen) {
        text.append(Random.nextInt(65, 122).toChar())
    }

    return text.toString()
}

