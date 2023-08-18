package br.com.oneguy.accountstream.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
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

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

fun LocalDateTime.toEpoch() : Long =
    ZonedDateTime.of(this, ZoneId.systemDefault()).toInstant().toEpochMilli()


fun String.fromDecimalToBigDecimal(scale: Int = 4) = BigDecimal(BigInteger(Base64.getDecoder().decode(this)), scale)

fun BigDecimal.toDecimalString(scale: Int = 4) : String {
    return Base64.getEncoder().encodeToString(this.multiply(BigDecimal.TEN.pow(scale)).toBigInteger().toByteArray())
}
