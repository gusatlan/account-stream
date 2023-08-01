package br.com.oneguy.accountstream.service

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountDTO
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
                .map { mapper.writeValueAsString(it) }
                .doOnNext {
                    logger.info("upsertBankAccountPersist: [PROCESSED] $it")
                }
                .doOnError {
                    logger.error("BankAccountService:upsertBankAccountPersist $it")
                }
        }
    }

}