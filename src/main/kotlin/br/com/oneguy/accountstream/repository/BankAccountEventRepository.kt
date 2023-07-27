package br.com.oneguy.accountstream.repository

import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.model.persist.id.BankAccountEventId
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface BankAccountEventRepository : ReactiveCrudRepository<BankAccountEvent, BankAccountEventId>