package br.com.oneguy.accountstream.model.dto.id

import br.com.oneguy.accountstream.util.cleanCodeText

class BankAccountEventIdDTO(
    customerId: String = "",
    accountId: String = "",
    eventId: String = ""
) {

    val customerId = cleanCodeText(customerId).lowercase()
    val accountId = cleanCodeText(accountId).lowercase()
    val eventId = cleanCodeText(eventId).lowercase()

    override fun equals(other: Any?) = other != null &&
            other is BankAccountEventIdDTO &&
            customerId == other.customerId &&
            accountId == other.accountId &&
            eventId == other.eventId

    override fun hashCode() = customerId.hashCode() or accountId.hashCode() or eventId.hashCode()
    override fun toString() = """{"customerId": "$customerId", "accountId": "$accountId", "eventId": "$eventId"}"""
}