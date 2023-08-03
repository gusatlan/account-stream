package br.com.oneguy.accountstream.model

import br.com.oneguy.accountstream.mapper.transform
import br.com.oneguy.accountstream.model.dto.BankAccountDTO
import br.com.oneguy.accountstream.model.dto.PersistRequestBankAccountDTO
import br.com.oneguy.accountstream.model.dto.id.BankAccountIdDTO
import br.com.oneguy.accountstream.model.persist.BankAccount
import br.com.oneguy.accountstream.model.persist.EventTypeEnum
import br.com.oneguy.accountstream.model.persist.id.BankAccountId
import br.com.oneguy.accountstream.util.mapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BankAccountTest {

    private fun createBankAccountIdDTO() = BankAccountIdDTO(
        customerId = "customer_01",
        accountId = "customer_01_account_01"
    )

    private fun createBankAccountId() = BankAccountId(
        customerId = "customer_01",
        accountId = "customer_01_account_01"
    )

    private fun createBankAccountDTO() = BankAccountDTO(
        id = createBankAccountIdDTO(),
        since = LocalDateTime.of(2023, 8, 1, 11, 4)
    )

    private fun createBankAccount() = BankAccount(
        id = createBankAccountId(),
        since = LocalDateTime.of(2023, 8, 1, 11, 4)
    )

    @Test
    fun shouldBeSameId() {
        val idDTO = createBankAccountIdDTO()
        val id = createBankAccountId()

        val jsonIdDTO = mapper.writeValueAsString(idDTO)
        val jsonId = mapper.writeValueAsString(id)

        val unmarshallIdDTO = mapper.readValue(jsonIdDTO, BankAccountIdDTO::class.java)
        val unmarshallId = mapper.readValue(jsonId, BankAccountId::class.java)

        Assertions.assertEquals(id, idDTO.transform())
        Assertions.assertEquals(idDTO, id.transform())

        Assertions.assertEquals(idDTO, unmarshallIdDTO)
        Assertions.assertEquals(id, unmarshallId)
    }

    @Test
    fun shouldMarshallUnmarshallBankAccount() {
        val dto = createBankAccountDTO()
        val obj = createBankAccount()

        val jsonDTO = mapper.writeValueAsString(dto)
        val json = mapper.writeValueAsString(obj)

        val unmarshallDTO = mapper.readValue(jsonDTO, BankAccountDTO::class.java)
        val unmarshall = mapper.readValue(json, BankAccount::class.java)

        Assertions.assertEquals(dto, obj.transform())
        Assertions.assertEquals(obj, dto.transform())

        Assertions.assertEquals(dto, unmarshallDTO)
        Assertions.assertEquals(obj, unmarshall)
    }


    @Test
    fun shouldMarshallUnmarshallPersistRequest() {
        val obj1 = PersistRequestBankAccountDTO(
            type = EventTypeEnum.INSERT,
            entity = BankAccountDTO(
                id = BankAccountIdDTO(
                    customerId = "abc",
                    accountId = "abc-def"
                ),
                since = LocalDateTime.of(2023, 7, 31, 0, 0)
            )
        )

        val json = mapper.writeValueAsString(obj1)
        val unmarshall = mapper.readValue(json, PersistRequestBankAccountDTO::class.java)

        Assertions.assertEquals(obj1.entity.id, unmarshall.entity.id)
        Assertions.assertEquals(obj1.entity, unmarshall.entity)
    }

}