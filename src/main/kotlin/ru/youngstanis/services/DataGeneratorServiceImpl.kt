package ru.youngstanis.services

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.youngstanis.constants.Constants
import ru.youngstanis.utills.generateBigintByRule
import ru.youngstanis.utills.generateBigintRange
import ru.youngstanis.utills.generateBooleanByRule
import ru.youngstanis.utills.generateIntegerByRule
import ru.youngstanis.utills.generateIntegerRange
import ru.youngstanis.utills.generateSmallintByRule
import ru.youngstanis.utills.generateSmallintRange
import ru.youngstanis.utills.generateStringRange
import ru.youngstanis.utills.generateTextByRule
import ru.youngstanis.utills.readAndClose
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.util.Optional
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

@Service
class DataGeneratorServiceImpl(

    @Value("\${file-storage.template}")
    private val templatePath: String,

    @Value("\${file-storage.generated-data}")
    private val generatedDataPath: String,

) : DataGeneratorService {

    override fun uploadTemplateFile(
        templateFile: MultipartFile
    ): Optional<String> {

        val uploadingDir = "$USER_DIR/$templatePath"

        try {

            if (!Paths.get(uploadingDir).isDirectory()) {
                Files.createDirectories(Paths.get(uploadingDir))
            }

            templateFile.transferTo(File("$uploadingDir/${templateFile.originalFilename}"))

            return Optional.of(templateFile.originalFilename)

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun downloadGeneratedFile(generatedFileName: String): Optional<Resource> {

        val downloadingDir = "$USER_DIR/$generatedDataPath"

        if (!Paths.get(downloadingDir).isDirectory()) {
            throw Exception("Generated data directory not found")
        }

        val path = Paths.get("$downloadingDir/$generatedFileName")

        if (!path.exists()) {
            throw Exception("File \"$generatedFileName\" not found")
        }

        return Optional.of(UrlResource(path.toUri()))
    }

    override fun generateTestData(
        templateFileName: String,
        rowCount: Int,
        separator: CharSequence,
    ): Optional<Resource> {

        val uploadingDir = "$USER_DIR/$templatePath"

        val inputFile = Paths.get("$uploadingDir/$templateFileName").toFile()

        if (!inputFile.exists()) {
            throw Exception("File \"$templateFileName\" not found")
        }

        val jsonString = inputFile.inputStream().readAndClose()
        val jsonObject = JSONObject(jsonString)

        val jsonMap = jsonObject.toMap()
            .mapValues {
                it.value.toString()
            }

        val generatorRange = IntRange(0, rowCount - 1)

        val outputFileName = outputFileName()

        val downloadingDir = "$USER_DIR/$generatedDataPath"

        if (!Paths.get(downloadingDir).isDirectory()) {
            Files.createDirectories(Paths.get(downloadingDir))
        }
        val outputFile = Files.createFile(Paths.get("$downloadingDir/$outputFileName")).toFile()

        fillOutputFile(outputFile, generatorRange, jsonMap, separator, rowCount)

        return Optional.of(UrlResource(outputFile.toURI()))
    }

    private fun outputFileName(): String {

        val prefix = "generated-data"

        val localDateTime = LocalDateTime.ofInstant(Instant.now(), Constants.zoneId)
        val middlePart = localDateTime.format(Constants.formatter)

        val extension = ".csv"

        return "${prefix}_$middlePart$extension"
    }

    private fun fillOutputFile(
        outputFile: File,
        generatorRange: IntRange,
        jsonMap: Map<String, String>,
        separator: CharSequence,
        rowCount: Int,
    ) {

        val columnRecordMap = mutableMapOf<String, List<String>>()

        try {

            val columns = jsonMap.keys

            columns.forEach { column ->

                val typeRule = jsonMap.getValue(column)

                val generatedValues = generateRecordsForColumn(typeRule, rowCount)

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
            outputFile.appendText(record.joinToString(separator = separator, postfix = "\n"))
        }

        println("File \"${outputFile.name}\" was successfully generated")
    }

    private fun generateRecordsForColumn(
        dataTypeRule: String,
        rowCount: Int,
    ): List<String> {

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
                    generatorCount = rowCount,
                )
            }

            "text" -> {
                generateTextByRule(
                    textLen = generateStringRange(range),
                    unique = unique,
                    oneValue = oneValue,
                    generatorCount = rowCount,
                )
            }

            "varchar" -> {
                generateTextByRule(
                    textLen = generateStringRange(range),
                    unique = unique,
                    oneValue = oneValue,
                    generatorCount = rowCount,
                )
            }

            "boolean" -> {
                generateBooleanByRule(
                    generatorCount = rowCount,
                )
            }

            "smallint" -> {
                generateSmallintByRule(
                    smallintLen = generateSmallintRange(range),
                    unique = unique,
                    generatorCount = rowCount,
                )
            }

            "bigint" -> {
                generateBigintByRule(
                    bigintLen = generateBigintRange(range),
                    unique = unique,
                    oneValue = oneValue?.toLong(),
                    generatorCount = rowCount,
                )
            }

            else -> throw RuntimeException("Data type $dataType is not supported!")
        }
    }

    companion object {
        private val USER_DIR = System.getProperty("user.dir")
    }
}
