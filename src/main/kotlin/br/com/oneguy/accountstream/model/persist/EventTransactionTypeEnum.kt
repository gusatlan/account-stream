package br.com.oneguy.accountstream.model.persist

enum class EventTransactionTypeEnum {
    DEPOSIT, WITHDRAWN;

    companion object {
        fun valueOf(ordinal: Int) :EventTransactionTypeEnum {
            return values().first { it.ordinal == ordinal }
        }
    }
}