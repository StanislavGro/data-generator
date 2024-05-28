package ru.one.files

import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

internal fun InputStream.readAndClose(charset: Charset = Charsets.UTF_8) =
    this.bufferedReader(charset).use { it.readText() }

fun OutputStream.writeAndClose(line: String, charset: Charset = Charsets.UTF_8) =
    this.bufferedWriter(charset).use { it.write(line) }

fun Int.toBoolean() = this != 0