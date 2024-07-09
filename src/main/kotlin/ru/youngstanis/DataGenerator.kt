package ru.youngstanis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DataGenerator

fun main(args: Array<String>) {
    runApplication<DataGenerator>(*args)
}