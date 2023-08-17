package br.com.oneguy.accountstream.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

val mapper = buildMapper()

fun buildMapper(): ObjectMapper {
    val mapper = ObjectMapper()
    mapper.registerModule(JavaTimeModule())
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return mapper
}


fun cleanCode(text: String?): String {
    return if (text != null) {
        val pattern = "\\D".toRegex()
        pattern.replace(text.trim(), "")
    } else {
        ""
    }
}

fun cleanCodeText(text: String?): String {
    return if (text != null) {
        val pattern = "\\W".toRegex()
        pattern.replace(text.trim(), "").uppercase()
    } else {
        ""
    }
}

fun cleanCodeTextWithoutUnderscore(text: String?): String {
    return if (text != null) {
        val pattern = "[_\\W]*".toRegex()
        pattern.replace(text.trim(), "").uppercase()
    } else {
        ""
    }
}

fun Long.toLocalDateTime() : LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}