package br.com.oneguy.accountstream.model.persist

import br.com.oneguy.accountstream.model.persist.id.BankAccountEventId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

@Document(collection = "account_events")
data class BankAccountEvent(
    @field:Id val id: BankAccountEventId,
    val type: EventTransactionTypeEnum,
    val date: LocalDateTime,
    val value: BigDecimal
) : Comparable<BankAccountEvent> {

    override fun equals(other: Any?) = other != null && other is BankAccountEvent && id == other.id
    override fun hashCode() = id.hashCode()
    override fun toString() = """{"id": $id, "type": "$type", "date": "$date", "value": $value}"""
    override fun compareTo(other: BankAccountEvent): Int {
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
}