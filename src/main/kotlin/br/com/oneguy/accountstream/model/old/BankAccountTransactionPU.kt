package br.com.oneguy.accountstream.model.old

import java.math.BigDecimal
import java.time.LocalDateTime

class BankAccountTransactionPU(
    val transactionId: Long? = null,
    val type: BankAccountTransactionEnum = BankAccountTransactionEnum.DEPOSIT,
    val date: LocalDateTime = LocalDateTime.now(),
    val value: BigDecimal = BigDecimal.ZERO,
    val account: BankAccountPU = BankAccountPU(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {

    override fun equals(other: Any?) =
        other != null && other is BankAccountTransactionPU && transactionId == other.transactionId

    override fun hashCode() = transactionId.hashCode()

    override fun toString() = """{"transactionId": $transactionId, "type": "$type", "date": "$date", "value": $value}"""
}