package ru.youngstanis.services

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.util.Optional

interface DataGeneratorService {

    fun uploadTemplateFile(templateFile: MultipartFile): Optional<String>

    fun downloadGeneratedFile(generatedFileName: String): Optional<Resource>

    fun generateTestData(
        templateFileName: String,
        rowCount: Int,
        separator: CharSequence,
    ): Optional<Resource>
}