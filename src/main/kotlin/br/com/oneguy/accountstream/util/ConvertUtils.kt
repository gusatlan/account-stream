package br.com.oneguy.accountstream.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.util.*

val mapper = buildMapper()

fun buildMapper(): ObjectMapper {
    val mapper = ObjectMapper()
    mapper.registerModule(JavaTimeModule())
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

fun clean(text: String?): String {
    return text?.trim()?.lowercase() ?: ""
}

fun createUUID() = cleanCodeText(UUID.randomUUID().toString()).trim().lowercase()
