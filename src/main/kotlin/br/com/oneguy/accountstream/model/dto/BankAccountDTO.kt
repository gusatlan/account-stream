package br.com.oneguy.accountstream.model.dto

import br.com.oneguy.accountstream.model.dto.id.BankAccountIdDTO
import java.math.BigDecimal
import java.time.LocalDateTime

data class BankAccountDTO(
    val id: BankAccountIdDTO  = BankAccountIdDTO(),
    val since: LocalDateTime = LocalDateTime.now(),
    val expiredAt: LocalDateTime? = null,
    val transactions: Collection<BankAccountEventDTO> = emptySet()
) {

    val balance = computeBalance()

    private fun computeBalance(): BigDecimal {
        return transactions
            .stream()
            .sorted()
            .map(BankAccountEventDTO::computeValue)
            .reduce { a, b -> a.add(b) }
            .orElse(BigDecimal.ZERO)
    }

    override fun equals(other: Any?) = other != null && other is BankAccountDTO && id == other.id
    override fun hashCode()=id.hashCode()
    override fun toString() = """{"id": $id, "since": "$since", "expiredAt": "$expiredAt"}"""
}