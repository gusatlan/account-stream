package br.com.oneguy.accountstream.model.persist.id

import br.com.oneguy.accountstream.util.cleanCodeText

class BankAccountId(
    customerId: String,
    accountId: String
) {

    val customerId = cleanCodeText(customerId).lowercase()
    val accountId = cleanCodeText(accountId).lowercase()

    override fun hashCode() = customerId.hashCode() or accountId.hashCode()
    override fun equals(other: Any?) =
        other != null && other is BankAccountId && customerId == other.customerId && accountId == other.accountId

    override fun toString() = """{"customerId": "$customerId", "accountId": "$accountId"}"""
}