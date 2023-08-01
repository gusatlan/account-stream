package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.model.dto.BankAccountEventDTO
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountEventDTO
import br.com.oneguy.accountstream.model.dto.id.BankAccountEventIdDTO
import br.com.oneguy.accountstream.model.persist.BankAccountEvent
import br.com.oneguy.accountstream.model.persist.EventTransactionTypeEnum
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.model.persist.id.BankAccountEventId
import br.com.oneguy.accountstream.util.mapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class BankAccountEventTest {

    private fun createBankAccountEventIdDTO() = BankAccountEventIdDTO(
        customerId = "customer_01",
        accountId = "account_01_customer_01",
        eventId = "event_01_account_01_customer_01"
    )

    private fun createBankAccountEventId() = BankAccountEventId(
        customerId = "customer_01",
        accountId = "account_01_customer_01",
        eventId = "event_01_account_01_customer_01"
    )

    private fun createBankAccountEventDTO() = BankAccountEventDTO(
        id = createBankAccountEventIdDTO(),
        type = EventTransactionTypeEnum.DEPOSIT,
        date = LocalDateTime.of(2023, 8, 1, 9, 51),
        value = BigDecimal("1432.79")
    )

    private fun createBankAccountEvent() = BankAccountEvent(
        id = createBankAccountEventId(),
        type = EventTransactionTypeEnum.DEPOSIT,
        date = LocalDateTime.of(2023, 8, 1, 9, 51),
        value = BigDecimal("1432.79")
    )

    @Test
    fun shouldBeSameId() {
        val idDTO = createBankAccountEventIdDTO()
        val id = createBankAccountEventId()

        Assertions.assertEquals(idDTO.customerId, id.customerId)
        Assertions.assertEquals(idDTO.accountId, id.accountId)
        Assertions.assertEquals(idDTO.eventId, id.eventId)

        Assertions.assertEquals(id, idDTO.transform())
        Assertions.assertEquals(id.transform(), idDTO)
    }

    @Test
    fun shouldMarshallUnmarshallId() {
        val idDTO = createBankAccountEventIdDTO()
        val id = createBankAccountEventId()

        val jsonIdDTO = mapper.writeValueAsString(idDTO)
        val jsonId = mapper.writeValueAsString(id)

        val unmarshallIdDTO = mapper.readValue(jsonIdDTO, BankAccountEventIdDTO::class.java)
        val unmarshallId = mapper.readValue(jsonId, BankAccountEventId::class.java)

        Assertions.assertEquals(idDTO, unmarshallIdDTO)
        Assertions.assertEquals(id, unmarshallId)
    }

    @Test
    fun shouldMarshallUnmarshall() {
        val dto = createBankAccountEventDTO()
        val obj = createBankAccountEvent()

        val jsonDTO = mapper.writeValueAsString(dto)
        val json = mapper.writeValueAsString(obj)

        val unmarshallDTO = mapper.readValue(jsonDTO, BankAccountEventDTO::class.java)
        val unmarshall = mapper.readValue(json, BankAccountEvent::class.java)

        Assertions.assertEquals(obj, dto.transform())
        Assertions.assertEquals(dto, unmarshallDTO)
        Assertions.assertEquals(obj, unmarshall)
    }

    @Test
    fun shouldMarshallUnmarshallPersistRequest() {
        val obj1 = PersistRequestBankAccountEventDTO(
            type = EventTypeEnum.INSERT,
            entity = BankAccountEventDTO(
                id = BankAccountEventIdDTO(
                    customerId = "abc",
                    accountId = "abc-def",
                    eventId = "abc-def-ghi"
                ),
                type = EventTransactionTypeEnum.DEPOSIT,
                date = LocalDateTime.of(2023, 7, 31, 15, 25),
                value = BigDecimal("1599.87")
            )
        )

        val json = mapper.writeValueAsString(obj1)
        val unmarshall = mapper.readValue(json, PersistRequestBankAccountEventDTO::class.java)

        Assertions.assertEquals(obj1.entity.id, unmarshall.entity.id)
        Assertions.assertEquals(obj1.entity, unmarshall.entity)
        Assertions.assertEquals(obj1.entity.value, unmarshall.entity.value)
    }
}