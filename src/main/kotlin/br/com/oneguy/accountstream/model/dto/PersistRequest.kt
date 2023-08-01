package br.com.oneguy.accountstream.model.dto

import br.com.oneguy.accountstream.model.persist.EventTypeEnum

abstract class PersistRequest<T:Any>(
    val type: EventTypeEnum,
    val entity: T
)