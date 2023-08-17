package br.com.oneguy.accountstream.model.kafkaconnect

class BankAccountTransactionMessage(
    val payload: BankAccountTransactionPayload = BankAccountTransactionPayload()
)