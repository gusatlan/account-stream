package br.com.oneguy.accountstream.model.persist.id

import br.com.oneguy.accountstream.util.cleanCodeText

class BankAccountEventId(
    customerId: String,
    accountId: String,
    eventId: String
) {

    val customerId = cleanCodeText(customerId)
    val accountId = cleanCodeText(accountId)
    val eventId = cleanCodeText(eventId)

    override fun equals(other: Any?) = other != null &&
            other is BankAccountEventId &&
            customerId == other.customerId &&
            accountId == other.accountId &&
            eventId == other.eventId

    override fun hashCode() = customerId.hashCode() or accountId.hashCode() or eventId.hashCode()
    override fun toString() = """{"customerId": "$customerId", "accountId": "$accountId", "eventId": "$eventId"}"""
}