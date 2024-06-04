package cc.green.recall.server

import cc.green.recall.server.services.PayAccountProto
import cc.green.recall.server.services.PayAccountRepo
import cc.green.recall.server.services.PayAccountService
import cc.green.recall.server.services.getEntityTableName
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate


class PayAccountTests : RecallApplicationTests() {

    @Autowired
    lateinit var service: PayAccountService

    @Autowired
    lateinit var repo: PayAccountRepo

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun testControllerProtoDateParamInValid() {
        val nullIdentifierProto = nullIdentifierProto()

        val response = restTemplate.postForEntity("/pay/account/add", nullIdentifierProto, Response::class.java)

        assertNull(response.body?.data)
        assertEquals(Response.ERROR, response.body?.msg)
        assertEquals(HasNullProtoException.REASON, (response.body?.error as Map<*, *>)["reason"])
    }

    fun nullIdentifierProto() = PayAccountProto(1, null, "", "", true)

    @BeforeEach
    @Transactional
    fun setUp() {
        repo.deleteAll()
    }

    @Test
    fun testNewObj() {
        val proto = PayAccountProto(null, "identifier1", null, null, true)

        val newPayAccount = service.newPayAccount(proto)

        assertNotNull(newPayAccount.id)
        assertEquals(proto.identifier, newPayAccount.identifier)
    }

    @Test
    fun testNewObjOnIdentifierAlreadyExist() {
        val proto1 = PayAccountProto(null, "identifier1", null, null, true)
        service.newPayAccount(proto1)
        val proto2 = PayAccountProto(null, "identifier1", null, null, true)

        val exception = assertThrowsExactly(AlreadyExistsEntityException::class.java) {
            service.newPayAccount(proto2)
        }

        assertEquals(AlreadyExistsEntityException.REASON, exception.reason)
        assertEquals(
            "${proto2.identifier} with table ${getEntityTableName(PayAccount::class)} already exists",
            exception.message
        )
    }

    @Test
    fun testUpdateObj() {
        val originProto = PayAccountProto(null, "identifier1", null, null, true)
        val payAccount = service.newPayAccount(originProto)
        val updatedProto = PayAccountProto(payAccount.id, "${payAccount.identifier}updated", null, null, true)

        val updatePayAccount = service.updatePayAccount(updatedProto)

        assertNotNull(updatePayAccount)
        assertEquals(payAccount.id, updatePayAccount.id)
        assertEquals(updatedProto.identifier, updatePayAccount.identifier)
    }

    @Test
    fun testDeleteObj() {
        val proto = PayAccountProto(1, "identifier1", null, null, true)

        service.delPayAccount(listOf(proto))

        assertFalse(repo.existsById(proto.id!!))
    }

}

