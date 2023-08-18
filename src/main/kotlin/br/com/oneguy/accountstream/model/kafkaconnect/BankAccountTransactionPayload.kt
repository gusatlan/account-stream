package br.com.oneguy.accountstream.model.kafkaconnect

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

class BankAccountTransactionPayload(
    @field:JsonProperty("transaction_id")
    val transactionId: Long? = null,
    @field:JsonProperty("transaction_type")
    val type: Int = -1,
    @field:JsonProperty("transaction_date")
    val date: Long = 0L,
    @field:JsonProperty("transaction_value")
    val value: String = "",
    @field:JsonProperty("account_id")
    val accountId: Long = -1,
    @field:JsonProperty("created_at")
    val createdAt: Long = 0L,
    @field:JsonProperty("updated_at")
    val updatedAt: Long = 0L
) {

    override fun equals(other: Any?) =
        other != null && other is BankAccountTransactionPayload && transactionId == other.transactionId

    override fun hashCode() = transactionId.hashCode()

    override fun toString() = """{"transactionId": $transactionId, "type": "$type", "date": "$date", "value": $value}"""
}