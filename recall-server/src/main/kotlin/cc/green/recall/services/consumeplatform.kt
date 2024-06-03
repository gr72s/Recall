package cc.green.recall.services

import cc.green.recall.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class ConsumePlatformProto(
    val id: Long?,
    val identifier: String?,
    val label: String?
)

@RestController
class ConsumePlatformController(val service: ConsumePlatformService) {

    @PostMapping("/consume/platform/add")
    fun addConsumePlatform(@RequestBody proto: ConsumePlatformProto): ResponseEntity<Response> {
        proto.identifier ?: throw HasNullProtoException("identifier")
        return success(service.newConsumePlatform(proto))
    }

    @PostMapping("/consume/platform/update")
    fun updateConsumePlatform(@RequestBody proto: ConsumePlatformProto): ResponseEntity<Response> {
        proto.id ?: throw HasNullProtoException("id")
        return success(service.updateConsumePlatform(proto))
    }

    @PostMapping("/consume/platform/del")
    fun delConsumePlatform(@RequestBody protos: List<ConsumePlatformProto>): ResponseEntity<Response> {
        protos.forEach { it.id ?: throw HasNullProtoException("id") }
        return success(service.delConsumePlatform(protos))
    }

    @GetMapping("/consume/platform")
    fun getConsumePlatform(): ResponseEntity<Response> {
        return success(service.getConsumePlatform())
    }

}

@Service
class ConsumePlatformService(val repo: ConsumePlatformRepo) {

    fun newConsumePlatform(proto: ConsumePlatformProto): ConsumePlatform {
        repo.findByIdentifier(proto.identifier!!)?.let {
            throw AlreadyExistsEntityException(proto.identifier, ConsumePlatform::class)
        }
        return updateOrCreateConsumePlatform(proto, null)
    }

    fun updateConsumePlatform(proto: ConsumePlatformProto): ConsumePlatform {
        val found = repo.findByIdOrThrow(proto.id!!)
        return updateOrCreateConsumePlatform(proto, found)
    }

    fun delConsumePlatform(protos: List<ConsumePlatformProto>): List<ConsumePlatformProto> {
        val filtered = protos.filter { it.id != null }
        repo.deleteAllById(filtered.map { it.id })
        return filtered
    }

    fun getConsumePlatform(): List<ConsumePlatform> {
        return repo.findAll()
    }

    @Throws(ServiceException::class)
    private fun updateOrCreateConsumePlatform(proto: ConsumePlatformProto, account: ConsumePlatform?): ConsumePlatform {
        val obj = (account ?: ConsumePlatform()).apply {
            identifier = proto.identifier
            label = proto.label ?: identifier
        }
        return repo.save(obj)
    }
}

@Repository
interface ConsumePlatformRepo : JpaRepository<ConsumePlatform, Long> {
    fun findByIdentifier(identifier: String): ConsumePlatform?
}

fun ConsumePlatformRepo.findByIdentifierOrThrow(identifier: String): ConsumePlatform =
    findByIdentifier(identifier) ?: throw NotFoundEntityException(identifier, getEntityTableName<ConsumePlatform>())