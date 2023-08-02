package br.com.oneguy.accountstream.mapper

import br.com.oneguy.accountstream.model.debezium.ChangeDbz
import br.com.oneguy.accountstream.model.debezium.PayloadDbz
import br.com.oneguy.accountstream.model.dto.BankAccountDTO
import br.com.oneguy.accountstream.model.dto.BankAccountEventDTO
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountDTO
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountEventDTO
import br.com.oneguy.accountstream.model.dto.id.BankAccountEventIdDTO
import br.com.oneguy.accountstream.model.dto.id.BankAccountIdDTO
import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.model.persist.EventTransactionTypeEnum
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.model.persist.id.BankAccountEventId
import br.com.oneguy.accountstream.model.persist.id.BankAccountId
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

fun ChangeDbz.transformBankAccountEvent(): BankAccountEventDTO {
    return BankAccountEventDTO(
        id = BankAccountEventIdDTO(
            customerId = getValue("customerId")!!,
            accountId = getValue("accountId")!!,
            eventId = getValue("eventId")!!
        ),
        type = getValue("type")?.let { EventTransactionTypeEnum.valueOf(it.trim().uppercase()) }
            ?: EventTransactionTypeEnum.DEPOSIT,
        date = LocalDateTime.parse(getValue("date")),
        value = BigDecimal(getValue("value"))
    )
}

fun ChangeDbz.transformBankAccount(): BankAccountDTO {
    return BankAccountDTO(
        id = BankAccountIdDTO(
            customerId = getValue("customerId")!!,
            accountId = getValue("accountId")!!
        ),
        since = LocalDateTime.parse(getValue("since")),
        expiredAt = getValue("expiredAt")?.let {
            LocalDateTime.parse(it)
        }
    )
}

fun PayloadDbz.transformPersistRequestBankAccount(): PersistRequestBankAccountDTO {
    return if (before != null && after != null) {
        PersistRequestBankAccountDTO(EventTypeEnum.UPDATE, after.transformBankAccount())
    } else if (before == null && after != null) {
        PersistRequestBankAccountDTO(EventTypeEnum.INSERT, after.transformBankAccount())
    } else if (before != null) {
        PersistRequestBankAccountDTO(EventTypeEnum.DELETE, before.transformBankAccount())
    } else {
        PersistRequestBankAccountDTO(EventTypeEnum.NONE, BankAccountDTO())
    }
}

fun PayloadDbz.transformPersistRequestBankAccountEvent(): PersistRequestBankAccountEventDTO {
    return if (before != null && after != null) {
        PersistRequestBankAccountEventDTO(EventTypeEnum.UPDATE, after.transformBankAccountEvent())
    } else if (before == null && after != null) {
        PersistRequestBankAccountEventDTO(EventTypeEnum.INSERT, after.transformBankAccountEvent())
    } else if (before != null) {
        PersistRequestBankAccountEventDTO(EventTypeEnum.DELETE, before.transformBankAccountEvent())
    } else {
        PersistRequestBankAccountEventDTO(EventTypeEnum.NONE, BankAccountEventDTO())
    }
}

// TODO Implementar stream do debezium
// TODO Teste manual debezium -> kafka
// TODO docker-compose contendo todos os apps, kafka, pgsql, mongo
// TODO Documentação






