package br.com.oneguy.accountstream.model.debezium

data class EventDbz(
    val payload:PayloadDbz = PayloadDbz()
)