package br.com.oneguy.accountstream.mapper

import br.com.oneguy.accountstream.model.dto.BankAccountDTO
import br.com.oneguy.accountstream.model.dto.BankAccountEventDTO
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountDTO
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountEventDTO
import br.com.oneguy.accountstream.model.dto.id.BankAccountEventIdDTO
import br.com.oneguy.accountstream.model.dto.id.BankAccountIdDTO
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountPayload
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountTransactionPayload
import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.model.persist.EventTransactionTypeEnum
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.model.persist.id.BankAccountEventId
import br.com.oneguy.accountstream.model.persist.id.BankAccountId
import br.com.oneguy.accountstream.util.fromDecimalToBigDecimal
import br.com.oneguy.accountstream.util.toLocalDateTime
import java.math.BigDecimal
import java.time.LocalDateTime

fun BankAccountId.transform(): BankAccountIdDTO {
    return BankAccountIdDTO(
        customerId = customerId,
        accountId = accountId
    )
}

fun BankAccountIdDTO.transform(): BankAccountId {
    return BankAccountId(
        customerId = customerId,
        accountId = accountId
    )
}

fun BankAccountEventId.transform(): BankAccountEventIdDTO {
    return BankAccountEventIdDTO(
        customerId = customerId,
        accountId = accountId,
        eventId = eventId
    )
}

fun BankAccountEventIdDTO.transform(): BankAccountEventId {
    return BankAccountEventId(
        customerId = customerId,
        accountId = accountId,
        eventId = eventId
    )
}

fun BankAccount.transform(events: Collection<BankAccountEventDTO> = emptySet()): BankAccountDTO {
    return BankAccountDTO(
        id = id.transform(),
        since = since,
        expiredAt = expiredAt,
        transactions = events
    )
}

fun BankAccountDTO.transform(): BankAccount {
    return BankAccount(
        id = id.transform(),
        since = since,
        expiredAt = expiredAt
    )
}

fun BankAccountEvent.transform(): BankAccountEventDTO {
    return BankAccountEventDTO(
        id = id.transform(),
        type = type,
        date = date,
        value = value
    )
}

fun BankAccountEventDTO.transform(): BankAccountEvent {
    return BankAccountEvent(
        id = id.transform(),
        type = type,
        date = date,
        value = value
    )
}

fun BankAccountPayload.transform(): BankAccountDTO {
    return BankAccountDTO(
        id = BankAccountIdDTO(
            customerId = customerId,
            accountId = accountId.toString()
        ),
        since = since.toLocalDateTime(),
        expiredAt = expiredAt?.toLocalDateTime()
    )
}

fun BankAccountPayload.transformPersistRequestBankAccount(): PersistRequestBankAccountDTO {
    return if (updatedAt != createdAt) {
        PersistRequestBankAccountDTO(EventTypeEnum.UPDATE, transform())
    } else if (createdAt == updatedAt) {
        PersistRequestBankAccountDTO(EventTypeEnum.INSERT, transform())
    } else {
        PersistRequestBankAccountDTO(EventTypeEnum.NONE, transform())
    }
}

fun BankAccountTransactionPayload.transform(): BankAccountEventDTO {
    return BankAccountEventDTO(
        id = BankAccountEventIdDTO(
            customerId = "",
            accountId = accountId.toString(),
            eventId = transactionId!!.toString()
        ),
        type = EventTransactionTypeEnum.valueOf(type),
        date = date.toLocalDateTime(),
        value = value.fromDecimalToBigDecimal()
    )
}

fun BankAccountTransactionPayload.transformPersistRequestBankAccountEvent(): PersistRequestBankAccountEventDTO {
    return if (createdAt != updatedAt) {
        PersistRequestBankAccountEventDTO(EventTypeEnum.UPDATE, transform())
    } else if (createdAt == updatedAt) {
        PersistRequestBankAccountEventDTO(EventTypeEnum.INSERT, transform())
    } else {
        PersistRequestBankAccountEventDTO(EventTypeEnum.NONE, transform())
    }
}




