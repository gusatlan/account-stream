package br.com.oneguy.accountstream.model.persist

import br.com.oneguy.accountstream.model.persist.id.BankAccountId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "accounts")
data class BankAccount(
    @field:Id val id: BankAccountId = BankAccountId(),
    val since: LocalDateTime = LocalDateTime.now(),
    val expiredAt: LocalDateTime? = null,
    val transactions: Collection<BankAccountEvent> = emptySet()
) {
    override fun equals(other: Any?) = other != null && other is BankAccount && id == other.id
    override fun hashCode() = id.hashCode()
    override fun toString() = """{"id": $id, "since": "$since", "expiredAt": "$expiredAt"}"""
}