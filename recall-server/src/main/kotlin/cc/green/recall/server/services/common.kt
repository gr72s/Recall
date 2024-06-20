package cc.green.recall.server.services

import cc.green.recall.server.Response
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger


@RestController
class Common {

    @Autowired
    lateinit var service: CommonService

    @GetMapping("/ping")
    fun ping() = success("pong")

    @GetMapping("/nextid/{tableName}")
    fun getNextId(@PathVariable tableName: String): ResponseEntity<Response> {
        val nextId = service.getNextId(tableName)
        return success(nextId)
    }
}

const val CS_PLATFORM = "cs_platform"
const val CS_PLATFORM_SEQUENCE = "cs_platform_id_seq"

const val CS_RECORD = "cs_record"
const val CS_RECORD_SEQUENCE = "cs_record_id_seq"

const val CS_TAG = "cs_tag"
const val CS_TAG_SEQUENCE = "cs_tag_id_seq"

const val PAY_ACCOUNT = "pay_account"
const val PAY_ACCOUNT_SEQUENCE = "pay_account_id_seq"

const val PAY_PLATFORM = "pay_platform"
const val PAY_PLATFORM_SEQUENCE = "pay_platform_id_seq"

@Service
class CommonService {

    @Autowired
    lateinit var entityManager: EntityManager

    fun getNextId(tableName: String): NextIdWrapper {
        val query = entityManager.createNativeQuery("select max(id) from :tableName")
        query.setParameter("tableName", tableNameMapSequenceName(tableName))
        val nextVal = query.singleResult as Long
        return NextIdWrapper(nextVal)
    }

    private fun tableNameMapSequenceName(tableName: String): String =
        when (tableName) {
            CS_PLATFORM -> CS_PLATFORM_SEQUENCE
            CS_RECORD -> CS_RECORD_SEQUENCE
            CS_TAG -> CS_TAG_SEQUENCE
            PAY_ACCOUNT -> PAY_ACCOUNT_SEQUENCE
            PAY_PLATFORM -> PAY_PLATFORM_SEQUENCE
            else -> throw IllegalArgumentException("Unknown table name: $tableName")
        }

}

data class NextIdWrapper(val nextId: Long)