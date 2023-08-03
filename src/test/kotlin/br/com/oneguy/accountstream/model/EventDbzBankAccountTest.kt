package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccount
import br.com.oneguy.accountstream.model.debezium.ChangeDbz
import br.com.oneguy.accountstream.model.debezium.PayloadDbz
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.util.mapper
import br.com.oneguy.accountstream.utils.storeJson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventDbzBankAccountTest {

    private fun createChangeDbz(
        customerId: String = "1",
        accountId: String = "11",
        since: LocalDateTime = LocalDateTime.of(2023, 8, 2, 14, 6),
        expiredAt: LocalDateTime? = null
    ): ChangeDbz {
        return ChangeDbz(
            mapOf(
                "customerId" to customerId,
                "accountId" to accountId,
                "since" to since.toString(),
                "expiredAt" to expiredAt?.toString()
            )
        )
    }

    private fun createPayloadCreate() = PayloadDbz(after = createChangeDbz())

    private fun createPayloadUpdate() = PayloadDbz(
        before = createChangeDbz(),
        after = createChangeDbz(expiredAt = LocalDateTime.of(2023, 8, 2, 15, 0))
    )

    private fun createPayloadDelete() = PayloadDbz(before = createChangeDbz())
    private fun createPayloadNone() = PayloadDbz(before = null, after = null)

    @Test
    fun shouldMarshallUnmarshall() {
        val obj1 = createPayloadCreate()
        val obj2 = createPayloadUpdate()
        val obj3 = createPayloadDelete()

        val json1 = mapper.writeValueAsString(obj1)
        val json2 = mapper.writeValueAsString(obj2)
        val json3 = mapper.writeValueAsString(obj3)

        val unmarshall1 = mapper.readValue(json1, PayloadDbz::class.java)
        val unmarshall2 = mapper.readValue(json2, PayloadDbz::class.java)
        val unmarshall3 = mapper.readValue(json3, PayloadDbz::class.java)

        Assertions.assertNotNull(obj1.after)
        Assertions.assertNull(obj1.before)
        Assertions.assertNotNull(unmarshall1.after)
        Assertions.assertNull(unmarshall1.before)

        Assertions.assertNotNull(obj2.after)
        Assertions.assertNotNull(obj2.before)
        Assertions.assertNotNull(unmarshall2.after)
        Assertions.assertNotNull(unmarshall2.before)

        Assertions.assertNull(obj3.after)
        Assertions.assertNotNull(obj3.before)
        Assertions.assertNull(unmarshall3.after)
        Assertions.assertNotNull(unmarshall3.before)

        Assertions.assertEquals(obj1.after?.getValue("customerId"), unmarshall1.after?.getValue("customerId"))
        Assertions.assertEquals(obj1.after?.getValue("accountId"), unmarshall1.after?.getValue("accountId"))
        Assertions.assertEquals(obj1.after?.getValue("since"), unmarshall1.after?.getValue("since"))
        Assertions.assertEquals(obj1.after?.getValue("expiredAt"), unmarshall1.after?.getValue("expiredAt"))

        storeJson(json=json1, prefixName = "bank_account_1")
        storeJson(json=json2, prefixName = "bank_account_2")
        storeJson(json=json3, prefixName = "bank_account_3")
    }

    @Test
    fun shouldTransformToPersistRequest() {
        val obj1 = createPayloadCreate().transformPersistRequestBankAccount()
        val obj2 = createPayloadUpdate().transformPersistRequestBankAccount()
        val obj3 = createPayloadDelete().transformPersistRequestBankAccount()
        val obj4 = createPayloadNone().transformPersistRequestBankAccount()

        Assertions.assertEquals(EventTypeEnum.INSERT, obj1.type)
        Assertions.assertEquals(EventTypeEnum.UPDATE, obj2.type)
        Assertions.assertEquals(EventTypeEnum.DELETE, obj3.type)
        Assertions.assertEquals(EventTypeEnum.NONE, obj4.type)
    }
}