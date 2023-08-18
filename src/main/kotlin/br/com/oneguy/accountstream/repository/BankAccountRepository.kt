package br.com.oneguy.accountstream.repository

import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.id.BankAccountId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface BankAccountRepository : ReactiveCrudRepository<BankAccount, BankAccountId> {
    fun findByIdAccountId(accountId: String) : Flux<BankAccount>
}