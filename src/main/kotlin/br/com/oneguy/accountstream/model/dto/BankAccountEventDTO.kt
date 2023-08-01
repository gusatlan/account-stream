package br.com.oneguy.accountstream.model.dto

import br.com.oneguy.accountstream.model.dto.id.BankAccountEventIdDTO
import br.com.oneguy.accountstream.model.persist.EventTransactionTypeEnum
import java.math.BigDecimal
import java.time.LocalDateTime

data class BankAccountEventDTO(
    val id: BankAccountEventIdDTO = BankAccountEventIdDTO(),
    val type: EventTransactionTypeEnum = EventTransactionTypeEnum.DEPOSIT,
    val date: LocalDateTime = LocalDateTime.now(),
    val value: BigDecimal = BigDecimal.ZERO
) : Comparable<BankAccountEventDTO> {
    override fun equals(other: Any?) = other != null && other is BankAccountEventDTO && id == other.id
    override fun hashCode() = id.hashCode()
    override fun toString() = """{"id": $id, "type": "$type", "date": "$date", "value": $value}"""

    override fun compareTo(other: BankAccountEventDTO): Int {
        val compares = listOf(
            id.customerId.compareTo(other.id.customerId),
            id.accountId.compareTo(other.id.accountId),
            date.compareTo(other.date)
        )
        var comp = 0

        for (c in compares) {
            comp = c
            if (comp != 0) {
                break
            }
        }

        return comp
    }

    fun computeValue(): BigDecimal {
        return when (type) {
            EventTransactionTypeEnum.DEPOSIT -> value.abs()
            EventTransactionTypeEnum.WITHDRAWN -> value.abs().negate()
        }
    }
}