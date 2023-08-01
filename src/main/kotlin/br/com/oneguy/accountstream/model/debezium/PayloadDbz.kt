package br.com.oneguy.accountstream.model.debezium

data class PayloadDbz(
    val before: ChangeDbz? = null,
    val after: ChangeDbz? = null
)