package ru.youngstanis.controllers


import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.youngstanis.services.DataGeneratorService


@RestController
@RequestMapping("/api/v1")
class DataGeneratorController(
    private val dataGeneratorService: DataGeneratorService,
) {

    @PostMapping("/uploadTemplateFile")
    fun uploadTemplateData(
        @RequestParam("templateFile") templateFile: MultipartFile,
    ): ResponseEntity<String> {

        val uploadedTemplateFileName = dataGeneratorService
            .uploadTemplateFile(templateFile)
            .orElseThrow()

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body("\"$uploadedTemplateFileName\" was successfully uploaded")
    }

    @GetMapping("/downloadGeneratedFile/{generatedFileName}")
    fun downloadTemplateData(
        @PathVariable("generatedFileName") generatedFileName: String,
    ): ResponseEntity<Resource> {

        val generatedFile = dataGeneratorService
            .downloadGeneratedFile(generatedFileName)
            .orElseThrow()

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + generatedFile.filename + "\""
            ).body(generatedFile)
    }

    @PostMapping("/generateDownloadTestData/{templateFileName}")
    fun generateAndDownloadTestData(
        @PathVariable("templateFileName") templateFileName: String,
        @RequestParam("rowCount") rowCount: Int,
        @RequestParam("separator") separator: CharSequence,
    ): ResponseEntity<Resource> {

        val generatedFile = dataGeneratorService.generateTestData(
            templateFileName = templateFileName,
            rowCount = rowCount,
            separator = separator,
        ).orElseThrow()

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + generatedFile.filename + "\""
            ).body(generatedFile)
    }

    @PostMapping("/uploadGenerateDownloadTestData")
    fun uploadGenerateAndDownloadTestData(
        @RequestParam("templateFile") templateFile: MultipartFile,
        @RequestParam("rowCount") rowCount: Int,
        @RequestParam("separator") separator: CharSequence,
    ): ResponseEntity<Resource> {

        val uploadedFileName = dataGeneratorService.uploadTemplateFile(templateFile).orElseThrow()

        val generatedFile = dataGeneratorService.generateTestData(
            templateFileName = uploadedFileName,
            rowCount = rowCount,
            separator = separator,
        ).orElseThrow()

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + generatedFile.filename + "\""
            ).body(generatedFile)
    }
}