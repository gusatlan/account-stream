package br.com.oneguy.accountstream.model.kafkaconnect

import br.com.oneguy.accountstream.model.persist.EventTransactionTypeEnum

enum class BankAccountTransactionEnum {
    DEPOSIT,
    WITHDRAWN;

    fun transform(): EventTransactionTypeEnum {
        return when (this) {
            DEPOSIT -> EventTransactionTypeEnum.DEPOSIT
            WITHDRAWN -> EventTransactionTypeEnum.WITHDRAWN
        }
    }
}