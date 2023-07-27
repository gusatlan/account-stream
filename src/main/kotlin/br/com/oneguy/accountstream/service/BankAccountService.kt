package br.com.oneguy.accountstream.service

import br.com.oneguy.accountstream.model.dto.PersistRequest
import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.repository.BankAccountRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BankAccountService(
    private val repository: BankAccountRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private fun save(value: BankAccount): Mono<BankAccount> {
        return repository
            .save(value)
            .doOnNext { logger.info("BankAccountService:save saved with success $value") }
            .doOnError { logger.error("BankAccountService:save error $value", it) }
    }

    private fun remove(value: BankAccount): Mono<Void> {
        return repository
            .delete(value)
            .doOnNext { logger.info("BankAccountService:remove removed with success $value") }
            .doOnError { logger.error("BankAccountService:remove error $value", it) }
    }

    fun upsert(value: PersistRequest<BankAccount>): Mono<BankAccount> {
        return Mono.just(value)
            .flatMap {
                if (it.type.isUpsert()) {
                    save(it.entity)
                } else {
                    remove(it.entity).thenReturn(it.entity)
                }
            }
    }
}