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

data class ConsumeTagProto(
    val id: Long?,
    val identifier: String?,
    val label: String?,
    val superior: Long?
)

@RestController
class ConsumeTagController(val service: ConsumeTagService) {

    @PostMapping("/consume/tag/add")
    fun addConsumeTag(@RequestBody proto: ConsumeTagProto): ResponseEntity<Response> {
        proto.identifier ?: throw HasNullProtoException("identifier")
        return success(service.newConsumeTag(proto))
    }

    @PostMapping("/consume/tag/update")
    fun updateConsumeTag(@RequestBody proto: ConsumeTagProto): ResponseEntity<Response> {
        proto.id ?: throw HasNullProtoException("id")
        return success(service.updateConsumeTag(proto))
    }

    @PostMapping("/consume/tag/del")
    fun delConsumeTag(@RequestBody protos: List<ConsumeTagProto>): ResponseEntity<Response> {
        protos.forEach { it.id ?: throw HasNullProtoException("id") }
        return success(service.delConsumeTag(protos))
    }

    @GetMapping("/consume/tag")
    fun getConsumeTag(): ResponseEntity<Response> {
        return success(service.getConsumeTag())
    }

}

@Service
class ConsumeTagService(val repo: ConsumeTagRepo) {

    fun newConsumeTag(proto: ConsumeTagProto): ConsumeTag {
        if (repo.existsByIdentifier(proto.identifier!!)) {
            throw AlreadyExistsEntityException(proto.identifier, ConsumeTag::class)
        }
        return updateOrCreateConsumeTag(proto, null)
    }

    fun updateConsumeTag(proto: ConsumeTagProto): ConsumeTag {
        val found = repo.findByIdOrThrow(proto.id!!)
        return updateOrCreateConsumeTag(proto, found)
    }

    fun delConsumeTag(protos: List<ConsumeTagProto>): List<ConsumeTagProto> {
        val filtered = protos.filter { it.id != null }
        repo.deleteAllById(filtered.map { it.id })
        return filtered
    }

    fun getConsumeTag(): List<ConsumeTag> {
        return repo.findAll()
    }

    @Throws(ServiceException::class)
    private fun updateOrCreateConsumeTag(proto: ConsumeTagProto, tag: ConsumeTag?): ConsumeTag {
        val obj = (tag ?: ConsumeTag()).apply {
            identifier = proto.identifier
            label = proto.label ?: proto.identifier
            superior = proto.superior?.let { repo.findById(it).orElse(null) }
        }
        return repo.save(obj)
    }
}

@Repository
interface ConsumeTagRepo : JpaRepository<ConsumeTag, Long> {
    fun findByIdentifier(identifier: String): ConsumeTag?
    fun existsByIdentifier(identifier: String): Boolean
}

fun ConsumeTagRepo.findByIdentifierOrThrow(identifier: String): ConsumeTag =
    findByIdentifier(identifier) ?: throw NotFoundEntityException(identifier, getEntityTableName<ConsumeTag>())