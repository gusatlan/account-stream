package br.com.oneguy.accountstream.model.kafkaconnect

data class BankAccountMessage(
    val payload:BankAccountPayload = BankAccountPayload()
)