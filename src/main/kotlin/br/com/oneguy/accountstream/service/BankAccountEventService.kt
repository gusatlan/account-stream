package br.com.oneguy.accountstream.service

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccountEvent
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountEventDTO
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountTransactionMessage
import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.model.persist.id.BankAccountEventId
import br.com.oneguy.accountstream.repository.BankAccountEventRepository
import br.com.oneguy.accountstream.repository.BankAccountRepository
import br.com.oneguy.accountstream.util.cleanCodeText
import br.com.oneguy.accountstream.util.mapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.function.Function

@Service
class BankAccountEventService(
    private val repository: BankAccountEventRepository,
    private val bankAccountRepository: BankAccountRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private fun findBankAccount(accountId: String): Mono<BankAccount> {
        return bankAccountRepository
            .findByIdAccountId(cleanCodeText(accountId).lowercase())
            .toMono()
            .doOnNext {
                logger.debug("findBankAccount [$accountId]: $it")
            }
    }

    private fun save(value: BankAccountEvent): Mono<BankAccountEvent> {
        return findBankAccount(value.id.accountId)
            .map {
                BankAccountEvent(
                    id = BankAccountEventId(
                        customerId = it.id.customerId,
                        accountId = value.id.accountId,
                        eventId = value.id.eventId
                    ),
                    type = value.type,
                    date = value.date,
                    value = value.value
                )
            }
            .flatMap(repository::save)
            .doOnNext {
                logger.info("BankAccountEventService:save saved with success $value")
            }
            .doOnError {
                logger.error("BankAccountEventService:save error $value", it)
            }
    }

    private fun remove(value: BankAccountEvent): Mono<Void> {
        return repository
            .delete(value)
            .doOnNext {
                logger.info("BankAccountEventService:remove removed with success $value")
            }
            .doOnError {
                logger.error("BankAccountEventService:remove error $value", it)
            }
    }

    private fun upsertStream(vararg values: PersistRequestBankAccountEventDTO): Flux<PersistRequestBankAccountEventDTO> {
        return Flux.fromIterable(values.asIterable())
            .flatMap { request ->
                val value = request.entity

                when (request.type) {
                    EventTypeEnum.INSERT, EventTypeEnum.UPDATE -> save(value.transform()).subscribe()
                    EventTypeEnum.DELETE -> remove(value.transform()).subscribe()
                    else -> logger.error("upsertBankAccountEventPersist: Event ${request.type}")
                }
                Mono.just(request)
            }
            .doOnNext {
                logger.info("upsertBankAccountEventPersist: [PROCESSED] $it")
            }
            .doOnError {
                logger.error("upsertBankAccountEventPersist: [ERROR] ", it)
            }
    }

    @Bean
    fun upsertBankAccountEventPersist(): Function<Flux<String>, Flux<String>> {
        return Function { request ->
            request.doOnNext {
                logger.info("upsertBankAccountEventPersist: [RECEIVED] $it")
            }
                .flatMap {
                    upsertStream(mapper.readValue(it, PersistRequestBankAccountEventDTO::class.java))
                }
                .map {
                    mapper.writeValueAsString(it)
                }
                .doOnNext {
                    logger.info("upsertBankAccountEventPersist: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountEventService:upsertBankAccountEventPersist $it")
                }
        }
    }

    @Bean
    fun transformLegacyBankAccountEventConnect(): Function<Flux<String>, Flux<String>> {
        return Function { dbEvent ->
            dbEvent.doOnNext {
                logger.info("BankAccountEventService:transformLegacyBankAccountEventConnect: [RECEIVED] $it")
            }
                .map {
                    mapper.readValue(it, BankAccountTransactionMessage::class.java).payload
                }
                .map {
                    it.transformPersistRequestBankAccountEvent()
                }
                .doOnNext {
                    logger.info("BankAccountEventService:transformLegacyBankAccountEventConnect: [TRANSFORMED] $it")
                }
                .map {
                    mapper.writeValueAsString(it)
                }
                .doOnNext {
                    logger.info("BankAccountEventService:transformLegacyBankAccountEventConnect: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountEventService:transformLegacyBankAccountEventConnect $it")
                }
        }
    }
}
