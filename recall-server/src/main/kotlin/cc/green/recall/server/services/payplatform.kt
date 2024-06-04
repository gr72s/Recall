package cc.green.recall.server.services

import cc.green.recall.*
import cc.green.recall.server.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class PayPlatformProto(
    val id: Long?,
    val identifier: String?,
    val label: String?,
)

@RestController
class PayPlatformController(val service: PayPlatformService) {

    @PostMapping("/pay/platform/add")
    fun addPayPlatform(@RequestBody proto: PayPlatformProto): ResponseEntity<Response> {
        proto.identifier ?: throw HasNullProtoException("identifier")
        return success(service.newPayPlatform(proto))
    }

    @PostMapping("/pay/platform/update")
    fun updatePayPlatform(@RequestBody proto: PayPlatformProto): ResponseEntity<Response> {
        proto.id ?: throw HasNullProtoException("id")
        return success(service.updatePayPlatform(proto))
    }

    @PostMapping("/pay/platform/del")
    fun delPayPlatform(@RequestBody protos: List<PayPlatformProto>): ResponseEntity<Response> {
        protos.forEach { it.id ?: throw HasNullProtoException("id") }
        return success(service.delPayPlatform(protos))
    }

    @GetMapping("/pay/platform")
    fun getPayPlatform(): ResponseEntity<Response> {
        return success(service.getPayPlatform())
    }

}

@Service
class PayPlatformService(val repo: PayPlatformRepo) {

    fun newPayPlatform(proto: PayPlatformProto): PayPlatform {
        if (repo.existsByIdentifier(proto.identifier!!)) {
            throw AlreadyExistsEntityException(proto.identifier, PayPlatform::class)
        }
        return updateOrCreatePayPlatform(proto, null)
    }

    fun updatePayPlatform(proto: PayPlatformProto): PayPlatform {
        val found = repo.findByIdOrThrow(proto.id!!)
        return updateOrCreatePayPlatform(proto, found)
    }

    fun delPayPlatform(protos: List<PayPlatformProto>): List<PayPlatformProto> {
        val filtered = protos.filter { it.id != null }
        repo.deleteAllById(filtered.map { it.id })
        return filtered
    }

    fun getPayPlatform(): List<PayPlatform> {
        return repo.findAll()
    }

    @Throws(ServiceException::class)
    private fun updateOrCreatePayPlatform(proto: PayPlatformProto, payPayPlatform: PayPlatform?): PayPlatform {
        val obj = (payPayPlatform ?: PayPlatform()).apply {
            identifier = proto.identifier
            label = proto.label ?: proto.identifier
        }
        return repo.save(obj)
    }
}

@Repository
interface PayPlatformRepo : JpaRepository<PayPlatform, Long> {
    fun findByIdentifier(identifier: String): PayPlatform?
    fun existsByIdentifier(identifier: String): Boolean
}

fun PayPlatformRepo.findByIdentifierOrThrow(identifier: String): PayPlatform =
    findByIdentifier(identifier) ?: throw NotFoundEntityException(identifier, getEntityTableName<PayPlatform>())