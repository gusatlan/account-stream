package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccountEvent
import br.com.oneguy.accountstream.model.old.BankAccountPU
import br.com.oneguy.accountstream.model.old.BankAccountTransactionEnum
import br.com.oneguy.accountstream.model.old.BankAccountTransactionPU
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.util.mapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class BankAccountEventPUTest {

    private fun createBankAccountEvent(
        customerId: String = "abc",
        accountId: Long = 333,
        transactionId: Long = 123,
        type: BankAccountTransactionEnum = BankAccountTransactionEnum.DEPOSIT,
        value: BigDecimal = BigDecimal("1543.28"),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updateAt: LocalDateTime? = null
    ): BankAccountTransactionPU {
        val date = LocalDateTime.of(2023, 8, 5, 8, 22)

        return BankAccountTransactionPU(
            account = BankAccountPU(
                customerId = customerId,
                accountId = accountId,
                since = date,
                createdAt = date,
                updatedAt = date
            ),
            transactionId = transactionId,
            type = type,
            date = date,
            value = value,
            createdAt = createdAt,
            updatedAt = updateAt
        )
    }

    @Test
    fun shouldMarshallUnmarshall() {
        val obj1 = createBankAccountEvent()
        val obj2 = createBankAccountEvent(
            type = BankAccountTransactionEnum.WITHDRAWN,
            value = BigDecimal("0.23"),
            updateAt = LocalDateTime.now()
        )

        val json1 = mapper.writeValueAsString(obj1)
        val json2 = mapper.writeValueAsString(obj2)

        val unmarshall1 = mapper.readValue(json1, BankAccountTransactionPU::class.java)
        val unmarshall2 = mapper.readValue(json2, BankAccountTransactionPU::class.java)

        Assertions.assertEquals(obj1, unmarshall1)
        Assertions.assertEquals(obj2, unmarshall2)
        Assertions.assertNull(obj1.updatedAt)
        Assertions.assertNotNull(obj2.updatedAt)
    }

    @Test
    fun shouldTransformPersistEvent() {
        val obj1 = createBankAccountEvent()
        val obj2 = createBankAccountEvent(
            type = BankAccountTransactionEnum.WITHDRAWN,
            value = BigDecimal("0.23"),
            updateAt = LocalDateTime.now()
        )

        val transform1 = obj1.transformPersistRequestBankAccountEvent()
        val transform2 = obj2.transformPersistRequestBankAccountEvent()

        Assertions.assertEquals(EventTypeEnum.INSERT, transform1.type)
        Assertions.assertEquals(EventTypeEnum.UPDATE, transform2.type)

        Assertions.assertEquals(obj1.transform(), transform1.entity)
        Assertions.assertEquals(obj2.transform(), transform2.entity)
    }
}