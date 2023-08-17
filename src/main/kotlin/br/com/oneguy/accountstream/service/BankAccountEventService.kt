package br.com.oneguy.accountstream.service

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccountEvent
import br.com.oneguy.accountstream.model.debezium.EventDbz
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountEventDTO
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountTransactionPayload
import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.repository.BankAccountEventRepository
import br.com.oneguy.accountstream.util.mapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

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
    fun transformLegacyBankAccountEvent(): Function<Flux<String>, Flux<String>> {
        return Function { dbEvent ->
            dbEvent.doOnNext {
                logger.info("BankAccountEventService:transformLegacyBankAccountEvent: [RECEIVED] $it")
            }
                .map {
                    mapper.readValue(it, EventDbz::class.java)
                }
                .map {
                    it.payload.transformPersistRequestBankAccountEvent()
                }
                .doOnNext {
                    logger.info("BankAccountEventService:transformLegacyBankAccountEvent: [TRANSFORMED] $it")
                }
                .map {
                    mapper.writeValueAsString(it)
                }
                .doOnNext {
                    logger.info("BankAccountEventService:transformLegacyBankAccountEvent: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountEventService:transformLegacyBankAccountEvent $it")
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
                    mapper.readValue(it, BankAccountTransactionPayload::class.java)
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