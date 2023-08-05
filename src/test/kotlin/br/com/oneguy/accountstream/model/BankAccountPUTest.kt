package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccount
import br.com.oneguy.accountstream.model.old.BankAccountPU
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.util.mapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BankAccountPUTest {

    private fun createBankAccount(updated: Boolean = false): BankAccountPU {
        val since = LocalDateTime.of(2023, 8, 5, 8, 10)

        return BankAccountPU(
            customerId = "zzz",
            accountId = 333,
            since = since,
            expiredAt = since.plusDays(10),
            createdAt = since,
            updatedAt = if (updated) since.plusDays(1) else null
        )
    }

    @Test
    fun shouldMarshallUnmarshall() {
        val obj1 = createBankAccount()
        val obj2 = createBankAccount(true)

        val json1 = mapper.writeValueAsString(obj1)
        val json2 = mapper.writeValueAsString(obj2)

        val unmarshall1 = mapper.readValue(json1, BankAccountPU::class.java)
        val unmarshall2 = mapper.readValue(json2, BankAccountPU::class.java)

        Assertions.assertEquals(obj1, unmarshall1)
        Assertions.assertEquals(obj2, unmarshall2)
        Assertions.assertNull(obj1.updatedAt)
        Assertions.assertNotNull(obj2.updatedAt)
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