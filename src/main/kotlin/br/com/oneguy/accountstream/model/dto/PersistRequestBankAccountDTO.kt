package br.com.oneguy.accountstream.model.dto

import br.com.oneguy.accountstream.model.persist.EventTypeEnum

class PersistRequestBankAccountDTO(
    type: EventTypeEnum = EventTypeEnum.INSERT,
    entity: BankAccountDTO = BankAccountDTO()
) : PersistRequest<BankAccountDTO>(type, entity)