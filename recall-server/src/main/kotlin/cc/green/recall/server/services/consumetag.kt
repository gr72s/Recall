package cc.green.recall.server.services

import cc.green.recall.*
import cc.green.recall.server.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

data class ConsumeTagProto(
    val id: Long?,
    val identifier: String?,
    val label: String?,
    val superior: Long?,
    val superiorIdentifier: String? = null
) {
    constructor() : this(null, null, null, null)
}

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
    fun getConsumeTag(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Response> {
        var pageParam = page
        if (pageParam < 1) pageParam = 1
        var sizeParam = size
        if (sizeParam < 0) sizeParam = 0
        pageParam -= 1
        return success(service.getConsumeTag(pageParam, sizeParam))
    }

}

@Service
class ConsumeTagService(val repo: ConsumeTagRepo) {

    fun newConsumeTag(proto: ConsumeTagProto): ConsumeTagProto {
        if (repo.existsByIdentifier(proto.identifier!!)) {
            throw AlreadyExistsEntityException(proto.identifier, ConsumeTag::class)
        }
        updateOrCreateConsumeTag(proto, null)
        return proto
    }

    fun updateConsumeTag(proto: ConsumeTagProto): ConsumeTagProto {
        val found = repo.findByIdOrThrow(proto.id!!)
        updateOrCreateConsumeTag(proto, found)
        return proto
    }

    fun delConsumeTag(protos: List<ConsumeTagProto>): List<ConsumeTagProto> {
        val filtered = protos.filter { it.id != null }
        repo.deleteAllById(filtered.map { it.id })
        return filtered
    }

    fun getConsumeTag(page: Int, size: Int): Pages<ConsumeTagProto> {
        val tags = repo.findAll(PageRequest.of(page, size, Sort.by("id")))
        val associateBy = tags.associateBy { it.id }
        val protos = tags.map {
            ConsumeTagProto(
                it.id,
                it.identifier,
                it.label,
                it.superior,
                associateBy[it.superior]?.identifier
            )
        }.toList()
        return Pages(tags, protos)
    }

    @Throws(ServiceException::class)
    private fun updateOrCreateConsumeTag(proto: ConsumeTagProto, tag: ConsumeTag?): ConsumeTag {
        val obj = (tag ?: ConsumeTag()).apply {
            identifier = proto.identifier
            label = proto.label
            superior = proto.superior
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