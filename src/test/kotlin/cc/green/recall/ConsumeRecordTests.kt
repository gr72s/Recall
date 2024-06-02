package cc.green.recall

import cc.green.recall.services.*
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.annotation.Rollback
import java.time.LocalDate

class ConsumeRecordTests : RecallApplicationTests() {

    @Autowired
    lateinit var consumeRecordService: ConsumeRecordService

    @Autowired
    lateinit var consumePlatformService: ConsumePlatformService

    @Autowired
    lateinit var consumeTagService: ConsumeTagService

    @Autowired
    lateinit var payAccountService: PayAccountService

    @Autowired
    lateinit var payMethodService: PayMethodService

    @Autowired
    lateinit var payPlatformService: PayPlatformService

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun testControllerProtoDateParamInValid() {

        val noDateProto = ConsumeRecordProto(null, 1, 1, 1.toBigDecimal(), 1, null, 1, listOf())

        val responseEntity = restTemplate.postForEntity(
            "/consume/record/add", noDateProto, Response::class.java
        )

        assertEquals(null, responseEntity.body?.data)
        assertEquals(Response.ERROR, responseEntity.body?.msg)
        assertEquals(HasNullProtoException.REASON, (responseEntity.body?.error as Map<*, *>)["reason"])
    }

    @Test
    fun testControllerNotFoundOtherEntity() {
        val absentEntityIdProto =
            ConsumeRecordProto(null, 1, null, 1.toBigDecimal(), null, LocalDate.now(), null, listOf())

        val responseEntity = restTemplate.postForEntity(
            "/consume/record/add", absentEntityIdProto, Response::class.java
        )

        assertEquals(null, responseEntity.body?.data)
        assertEquals(Response.ERROR, responseEntity.body?.msg)
        assertEquals(NotFoundEntityException.REASON, (responseEntity.body?.error as Map<*, *>)["reason"])
    }

    @Test
    fun testServiceNotFoundOtherEntity() {
        val recordProto = ConsumeRecordProto(1, 1, 1, 1.toBigDecimal(), 1, LocalDate.now(), 1, listOf())

        assertThrows<NotFoundEntityException>(NotFoundEntityException(1.toString(), "platform").message!!) {
            consumeRecordService.newConsumeRecord(recordProto)
        }
    }

    @Test
    @Rollback
    @Transactional
    fun tesNewObj() {
        val newConsumePlatform =
            consumePlatformService.newConsumePlatform(ConsumePlatformProto(null, "platform1", null))
        val newConsumeTag = consumeTagService.newConsumeTag(ConsumeTagProto(null, "tag1", null, null))
        val newPayAccount = payAccountService.newPayAccount(PayAccountProto(null, "account1", null, null, false))
        val newPayMethod = payMethodService.newPayMethod(PayMethodProto(null, "paymethod1", null))
        val newPayPlatform = payPlatformService.newPayPlatform(PayPlatformProto(null, "payplatform1", null))

        val newConsumeRecord = consumeRecordService.newConsumeRecord(
            ConsumeRecordProto(
                null,
                newPayPlatform.id,
                newPayMethod.id,
                10.toBigDecimal(),
                newPayAccount.id,
                LocalDate.now(),
                newConsumePlatform.id,
                listOf(newConsumeTag.id!!)
            )
        )

        assertNotNull(newConsumeRecord.id)
        assertEquals(newConsumePlatform.id, newConsumeRecord.consumePlatform!!.id)
        assertEquals(1, newConsumeRecord.tags.size)
        assertEquals(newConsumeTag.id, newConsumeRecord.tags.stream().findFirst().get().id)
        assertEquals(newPayAccount.id, newConsumeRecord.payAccount!!.id)
        assertEquals(newPayMethod.id, newConsumeRecord.payMethod!!.id)
        assertEquals(newPayPlatform.id, newConsumeRecord.payPlatform!!.id)
    }

}