package br.com.oneguy.accountstream.repository

import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.id.BankAccountId
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface BankAccountRepository : ReactiveCrudRepository<BankAccount, BankAccountId>