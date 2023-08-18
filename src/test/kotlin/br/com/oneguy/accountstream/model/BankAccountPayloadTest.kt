package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccount
import br.com.oneguy.accountstream.model.kafkaconnect.BankAccountPayload
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.util.mapper
import br.com.oneguy.accountstream.util.toEpoch
import br.com.oneguy.accountstream.utils.storeJson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BankAccountPayloadTest {

    private fun createBankAccount(updated: Boolean = false): BankAccountPayload {
        val since = LocalDateTime.of(2023, 8, 5, 8, 10)

        return BankAccountPayload(
            customerId = "zzz",
            accountId = 333,
            since = since.toEpoch(),
            expiredAt = since.plusDays(10).toEpoch(),
            createdAt = since.toEpoch(),
            updatedAt = if (updated) since.plusDays(1).toEpoch() else since.toEpoch()
        )
    }

    @Test
    fun shouldMarshallUnmarshall() {
        val obj1 = createBankAccount()
        val obj2 = createBankAccount(true)

        val json1 = mapper.writeValueAsString(obj1)
        val json2 = mapper.writeValueAsString(obj2)

        val unmarshall1 = mapper.readValue(json1, BankAccountPayload::class.java)
        val unmarshall2 = mapper.readValue(json2, BankAccountPayload::class.java)

        Assertions.assertEquals(obj1, unmarshall1)
        Assertions.assertEquals(obj2, unmarshall2)
        Assertions.assertEquals(obj1.updatedAt, obj1.createdAt)
        Assertions.assertNotNull(obj2.updatedAt)

        storeJson(json=json1, prefixName = "bank_account_pu_1")
        storeJson(json=json2, prefixName = "bank_account_pu_2")
    }

    @Test
    fun shouldTransformPersistEvent() {
        val obj1 = createBankAccount()
        val obj2 = createBankAccount(true)

        val transform1 = obj1.transformPersistRequestBankAccount()
        val transform2 = obj2.transformPersistRequestBankAccount()

        Assertions.assertEquals(EventTypeEnum.INSERT, transform1.type)
        Assertions.assertEquals(EventTypeEnum.UPDATE, transform2.type)

        Assertions.assertEquals(obj1.transform(), transform1.entity)
        Assertions.assertEquals(obj2.transform(), transform2.entity)
    }
}