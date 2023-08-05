package br.com.oneguy.accountstream.model.old

import java.time.LocalDateTime

class BankAccountPU(
    val accountId: Long? = null,
    val customerId: String = "",
    val since: LocalDateTime = LocalDateTime.now(),
    val expiredAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {

    override fun equals(other: Any?) = other != null && other is BankAccountPU && accountId == other.accountId
    override fun hashCode() = accountId.hashCode()
    override fun toString() =
        """{"accountId": $accountId, "customerId": "$customerId", "since": "$since", "expiredAt" : "$expiredAt"}"""
}