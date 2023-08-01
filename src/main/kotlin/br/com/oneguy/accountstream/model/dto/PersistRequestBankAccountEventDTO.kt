package br.com.oneguy.accountstream.model.dto

import br.com.oneguy.accountstream.model.persist.EventTypeEnum

class PersistRequestBankAccountEventDTO(
    type: EventTypeEnum = EventTypeEnum.INSERT,
    entity: BankAccountEventDTO = BankAccountEventDTO()
) : PersistRequest<BankAccountEventDTO>(type, entity)