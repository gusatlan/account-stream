package br.com.oneguy.accountstream.service

import br.com.oneguy.accountstream.model.dto.PersistRequest
import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.repository.BankAccountEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BankAccountEventService(
    private val repository: BankAccountEventRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private fun save(value: BankAccountEvent): Mono<BankAccountEvent> {
        return repository
            .save(value)
            .doOnNext { logger.info("BankAccountEventService:save saved with success $value") }
            .doOnError { logger.error("BankAccountEventService:save error $value", it) }
    }

    private fun remove(value: BankAccountEvent): Mono<Void> {
        return repository
            .delete(value)
            .doOnNext { logger.info("BankAccountEventService:remove removed with success $value") }
            .doOnError { logger.error("BankAccountEventService:remove error $value", it) }
    }

    fun upsert(value: PersistRequest<BankAccountEvent>): Mono<BankAccountEvent> {
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