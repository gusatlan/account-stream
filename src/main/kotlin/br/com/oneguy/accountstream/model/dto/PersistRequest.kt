package br.com.oneguy.accountstream.model.dto

import br.com.oneguy.accountstream.model.persist.EventTypeEnum

data class PersistRequest<T:Any>(
    val type: EventTypeEnum,
    val entity: T
)