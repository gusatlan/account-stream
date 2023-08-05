package br.com.oneguy.accountstream.service

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccount
import br.com.oneguy.accountstream.model.debezium.EventDbz
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountDTO
import br.com.oneguy.accountstream.model.old.BankAccountPU
import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.repository.BankAccountRepository
import br.com.oneguy.accountstream.util.mapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

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
            .doOnNext {
                logger.info("BankAccountService:save saved with success $value")
            }
            .doOnError {
                logger.error("BankAccountService:save error $value", it)
            }
    }

    private fun remove(value: BankAccount): Mono<Void> {
        return repository
            .delete(value)
            .doOnNext {
                logger.info("BankAccountService:remove removed with success $value")
            }
            .doOnError {
                logger.error("BankAccountService:remove error $value", it)
            }
    }

    private fun upsertStream(vararg values: PersistRequestBankAccountDTO): Flux<PersistRequestBankAccountDTO> {
        return Flux.fromIterable(values.asIterable())
            .flatMap { request ->
                val value = request.entity

                when (request.type) {
                    EventTypeEnum.INSERT, EventTypeEnum.UPDATE -> save(value.transform()).subscribe()
                    EventTypeEnum.DELETE -> remove(value.transform()).subscribe()
                    else -> logger.error("upsertBankAccountPersist: Event ${request.type}")
                }
                Mono.just(request)
            }
            .doOnNext {
                logger.info("upsertBankAccountPersist: [PROCESSED] $it")
            }
            .doOnError {
                logger.error("upsertBankAccountPersist: [ERROR] ", it)
            }
    }

    @Bean
    fun upsertBankAccountPersist(): Function<Flux<String>, Flux<String>> {
        /*
            Como o objeto está vindo da fila do kafka,
            e está em um pacote diferente desse projeto, é necessário receber a mensagem como String (json)
            e realizar o marshall
         */
        return Function { request ->
            request.doOnNext {
                logger.info("upsertBankAccountPersist: [RECEIVED] $it")
            }
                .flatMap {
                    upsertStream(mapper.readValue(it, PersistRequestBankAccountDTO::class.java))
                }
                .map {
                    mapper.writeValueAsString(it)
                }
                .doOnNext {
                    logger.info("upsertBankAccountPersist: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountService:upsertBankAccountPersist $it")
                }
        }
    }

    @Bean
    fun transformLegacyBankAccount(): Function<Flux<String>, Flux<String>> {
        return Function { dbEvent ->
            dbEvent.doOnNext {
                logger.info("BankAccountService:transformLegacyBankAccount: [RECEIVED] $it")
            }
                .map {
                    mapper.readValue(it, EventDbz::class.java)
                }
                .map {
                    it.payload.transformPersistRequestBankAccount()
                }
                .doOnNext {
                    logger.info("BankAccountService:transformLegacyBankAccount: [TRANSFORMED] $it")
                }
                .map {
                    mapper.writeValueAsString(it)
                }
                .doOnNext {
                    logger.info("BankAccountService:transformLegacyBankAccount: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountService:transformLegacyBankAccount $it")
                }
        }
    }

    @Bean
    fun transformLegacyBankAccountConnect(): Function<Flux<String>, Flux<String>> {
        return Function { dbEvent ->
            dbEvent.doOnNext {
                logger.info("BankAccountService:transformLegacyBankAccountConnect: [RECEIVED] $it")
            }
                .map {
                    mapper.readValue(it, BankAccountPU::class.java)
                }
                .map {
                    it.transformPersistRequestBankAccount()
                }
                .doOnNext {
                    logger.info("BankAccountService:transformLegacyBankAccountConnect: [TRANSFORMED] $it")
                }
                .map {
                    mapper.writeValueAsString(it)
                }
                .doOnNext {
                    logger.info("BankAccountService:transformLegacyBankAccountConnect: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountService:transformLegacyBankAccountConnect $it")
                }
        }
    }

}