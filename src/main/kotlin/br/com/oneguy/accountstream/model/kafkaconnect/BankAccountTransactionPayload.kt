package br.com.oneguy.accountstream.model.kafkaconnect

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

class BankAccountTransactionPayload(
    val transactionId: Long? = null,
    val type: BankAccountTransactionEnum = BankAccountTransactionEnum.DEPOSIT,
    val date: LocalDateTime = LocalDateTime.now(),
    val value: BigDecimal = BigDecimal.ZERO,
    val account: BankAccountPayload = BankAccountPayload(),
    @field:JsonProperty("created_at")
    val createdAt: LocalDateTime? = null,
    @field:JsonProperty("updated_at")
    val updatedAt: LocalDateTime? = null
) {

    override fun equals(other: Any?) =
        other != null && other is BankAccountTransactionPayload && transactionId == other.transactionId

    override fun hashCode() = transactionId.hashCode()

    override fun toString() = """{"transactionId": $transactionId, "type": "$type", "date": "$date", "value": $value}"""
}