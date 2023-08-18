package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccountEvent
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountTransactionEnum
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountTransactionPayload
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.util.mapper
import br.com.oneguy.accountstream.util.toDecimalString
import br.com.oneguy.accountstream.util.toEpoch
import br.com.oneguy.accountstream.utils.storeJson
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
        value: BigDecimal = BigDecimal("1543.2800"),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updateAt: LocalDateTime? = null
    ): BankAccountTransactionPayload {
        val date = LocalDateTime.of(2023, 8, 5, 8, 22)

        return BankAccountTransactionPayload(
            transactionId = transactionId,
            accountId = accountId,
            type = type.ordinal,
            createdAt = createdAt.toEpoch(),
            date = date.toEpoch(),
            updatedAt = updateAt?.toEpoch() ?: createdAt.toEpoch(),
            value = value.toDecimalString()
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

        val unmarshall1 = mapper.readValue(json1, BankAccountTransactionPayload::class.java)
        val unmarshall2 = mapper.readValue(json2, BankAccountTransactionPayload::class.java)

        Assertions.assertEquals(obj1, unmarshall1)
        Assertions.assertEquals(obj2, unmarshall2)
        Assertions.assertNotNull(obj1.updatedAt)
        Assertions.assertNotNull(obj2.updatedAt)

        storeJson(json = json1, prefixName = "bank_account_event_pu_1")
        storeJson(json = json2, prefixName = "bank_account_event_pu_2")
    }

    @Test
    fun shouldTransformPersistEvent() {
        val value = BigDecimal("0.2300")
        val obj1 = createBankAccountEvent()
        val obj2 = createBankAccountEvent(
            type = BankAccountTransactionEnum.WITHDRAWN,
            value = value,
            updateAt = LocalDateTime.now().plusHours(2L)
        )

        val transform1 = obj1.transformPersistRequestBankAccountEvent()
        val transform2 = obj2.transformPersistRequestBankAccountEvent()

        Assertions.assertEquals(EventTypeEnum.INSERT, transform1.type)
        Assertions.assertEquals(EventTypeEnum.UPDATE, transform2.type)

        Assertions.assertEquals(obj1.transform(), transform1.entity)
        Assertions.assertEquals(obj2.transform(), transform2.entity)

        Assertions.assertEquals(value, transform2.entity.value)
    }
}