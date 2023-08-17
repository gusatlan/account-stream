package br.com.oneguy.accountstream.model.kafkaconnect

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class BankAccountPayload(
    @field:JsonProperty("id")
    val accountId: Long? = null,
    @field:JsonProperty("customer_id")
    val customerId: String = "",
    val since: Long = 0L,
    @field:JsonProperty("expired")
    val expiredAt: Long? = null,
    @field:JsonProperty("created_at")
    val createdAt: Long = 0L,
    @field:JsonProperty("updated_at")
    val updatedAt: Long = 0L
) {

    override fun equals(other: Any?) = other != null && other is BankAccountPayload && accountId == other.accountId
    override fun hashCode() = accountId.hashCode()
    override fun toString() =
        """{"accountId": $accountId, "customerId": "$customerId", "since": "$since", "expiredAt" : "$expiredAt"}"""
}