package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transformPersistRequestBankAccountEvent
import br.com.oneguy.accountstream.model.debezium.ChangeDbz
import br.com.oneguy.accountstream.model.debezium.EventDbz
import br.com.oneguy.accountstream.model.debezium.PayloadDbz
import br.com.oneguy.accountstream.model.persist.EventTransactionTypeEnum
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.util.mapper
import br.com.oneguy.accountstream.utils.storeJson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class EventDbzBankAccountEventTest {

    private fun createChangeDbz(
        type: EventTransactionTypeEnum = EventTransactionTypeEnum.DEPOSIT,
        value: BigDecimal = BigDecimal.ZERO
    ): ChangeDbz {
        val map = mapOf(
            "customerId" to "1",
            "accountId" to "11",
            "eventId" to "111",
            "type" to type.toString(),
            "date" to LocalDateTime.of(2023, 8, 1, 15, 15).toString(),
            "value" to value.toString()
        )

        return ChangeDbz(map)
    }

    private fun createPayloadCreate() = PayloadDbz(after = createChangeDbz(value = BigDecimal("1444.22")))

    private fun createPayloadUpdate() = PayloadDbz(
        before = createChangeDbz(value = BigDecimal("1444.22")),
        after = createChangeDbz(type = EventTransactionTypeEnum.WITHDRAWN, value = BigDecimal("1444.22"))
    )

    private fun createPayloadDelete() = PayloadDbz(
        before = createChangeDbz(type = EventTransactionTypeEnum.WITHDRAWN, value = BigDecimal("1444.22"))
    )

    private fun createPayloadNone() = PayloadDbz()


    @Test
    fun shouldGetValue() {
        Assertions.assertEquals("1", createPayloadCreate().after!!.getValue("customer_id"))
        Assertions.assertNull(createPayloadCreate().after!!.getValue("bla"))
    }

    @Test
    fun shouldMarshallUnmarshallEventBankAccountEvent() {
        val obj1 = EventDbz(createPayloadCreate())
        val obj2 = EventDbz(createPayloadUpdate())
        val obj3 = EventDbz(createPayloadDelete())

        val json1 = mapper.writeValueAsString(obj1)
        val json2 = mapper.writeValueAsString(obj2)
        val json3 = mapper.writeValueAsString(obj3)

        val unmarshall1 = mapper.readValue(json1, EventDbz::class.java)
        val unmarshall2 = mapper.readValue(json2, EventDbz::class.java)
        val unmarshall3 = mapper.readValue(json3, EventDbz::class.java)

        Assertions.assertNull(obj1.payload.before)
        Assertions.assertNull(unmarshall1.payload.before)
        Assertions.assertNotNull(obj1.payload.after)
        Assertions.assertNotNull(unmarshall1.payload.after)

        Assertions.assertNotNull(obj2.payload.before)
        Assertions.assertNotNull(unmarshall2.payload.before)
        Assertions.assertNotNull(obj2.payload.after)
        Assertions.assertNotNull(unmarshall2.payload.after)

        Assertions.assertNull(obj3.payload.after)
        Assertions.assertNull(unmarshall3.payload.after)
        Assertions.assertNotNull(obj3.payload.before)
        Assertions.assertNotNull(unmarshall3.payload.before)

        Assertions.assertEquals(
            obj1.payload.after!!.changes["customerId"],
            unmarshall1.payload.after!!.changes["customerId"]
        )
        Assertions.assertEquals(
            obj1.payload.after!!.changes["accountId"],
            unmarshall1.payload.after!!.changes["accountId"]
        )
        Assertions.assertEquals(obj1.payload.after!!.changes["eventId"], unmarshall1.payload.after!!.changes["eventId"])
        Assertions.assertEquals(obj1.payload.after!!.changes["type"], unmarshall1.payload.after!!.changes["type"])
        Assertions.assertEquals(obj1.payload.after!!.changes["date"], unmarshall1.payload.after!!.changes["date"])
        Assertions.assertEquals(obj1.payload.after!!.changes["value"], unmarshall1.payload.after!!.changes["value"])

        Assertions.assertEquals(
            obj2.payload.before!!.changes["customerId"],
            unmarshall2.payload.before!!.changes["customerId"]
        )
        Assertions.assertEquals(
            obj2.payload.before!!.changes["accountId"],
            unmarshall2.payload.before!!.changes["accountId"]
        )
        Assertions.assertEquals(
            obj2.payload.before!!.changes["eventId"],
            unmarshall2.payload.before!!.changes["eventId"]
        )
        Assertions.assertEquals(obj2.payload.before!!.changes["type"], unmarshall2.payload.before!!.changes["type"])
        Assertions.assertEquals(obj2.payload.before!!.changes["date"], unmarshall2.payload.before!!.changes["date"])
        Assertions.assertEquals(obj2.payload.before!!.changes["value"], unmarshall2.payload.before!!.changes["value"])

        Assertions.assertEquals(
            obj2.payload.after!!.changes["customerId"],
            unmarshall2.payload.after!!.changes["customerId"]
        )
        Assertions.assertEquals(
            obj2.payload.after!!.changes["accountId"],
            unmarshall2.payload.after!!.changes["accountId"]
        )
        Assertions.assertEquals(obj2.payload.after!!.changes["eventId"], unmarshall2.payload.after!!.changes["eventId"])
        Assertions.assertEquals(obj2.payload.after!!.changes["type"], unmarshall2.payload.after!!.changes["type"])
        Assertions.assertEquals(obj2.payload.after!!.changes["date"], unmarshall2.payload.after!!.changes["date"])
        Assertions.assertEquals(obj2.payload.after!!.changes["value"], unmarshall2.payload.after!!.changes["value"])

        Assertions.assertEquals(
            obj3.payload.before!!.changes["customerId"],
            unmarshall3.payload.before!!.changes["customerId"]
        )
        Assertions.assertEquals(
            obj3.payload.before!!.changes["accountId"],
            unmarshall3.payload.before!!.changes["accountId"]
        )
        Assertions.assertEquals(
            obj3.payload.before!!.changes["eventId"],
            unmarshall3.payload.before!!.changes["eventId"]
        )
        Assertions.assertEquals(obj3.payload.before!!.changes["type"], unmarshall3.payload.before!!.changes["type"])
        Assertions.assertEquals(obj3.payload.before!!.changes["date"], unmarshall3.payload.before!!.changes["date"])
        Assertions.assertEquals(obj3.payload.before!!.changes["value"], unmarshall3.payload.before!!.changes["value"])

        storeJson(json=json1, prefixName = "bank_account_event_1")
        storeJson(json=json2, prefixName = "bank_account_event_2")
        storeJson(json=json3, prefixName = "bank_account_event_3")
    }

    @Test
    fun shouldTransformToPersistRequest() {
        val obj1 = createPayloadCreate().transformPersistRequestBankAccountEvent()
        val obj2 = createPayloadUpdate().transformPersistRequestBankAccountEvent()
        val obj3 = createPayloadDelete().transformPersistRequestBankAccountEvent()
        val obj4 = createPayloadNone().transformPersistRequestBankAccountEvent()

        Assertions.assertEquals(EventTypeEnum.INSERT, obj1.type)
        Assertions.assertEquals(EventTypeEnum.UPDATE, obj2.type)
        Assertions.assertEquals(EventTypeEnum.DELETE, obj3.type)
        Assertions.assertEquals(EventTypeEnum.NONE, obj4.type)
    }
}