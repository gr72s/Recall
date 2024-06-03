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

data class PayAccountProto(
    val id: Long?, val identifier: String?, val label: String?, val type: String?, val inUse: Boolean
) {
    constructor() : this(null, null, null, null, false)
}

@RestController
class PayAccountController(val service: PayAccountService) {

    @PostMapping("/pay/account/add")
    fun addPayAccount(@RequestBody proto: PayAccountProto): ResponseEntity<Response> {
        proto.identifier ?: throw HasNullProtoException("identifier")
        return success(service.newPayAccount(proto))
    }

    @PostMapping("/pay/account/update")
    fun updatePayAccount(@RequestBody proto: PayAccountProto): ResponseEntity<Response> {
        proto.id ?: throw HasNullProtoException("id")
        return success(service.updatePayAccount(proto))
    }

    @PostMapping("/pay/account/del")
    fun delPayAccount(@RequestBody protos: List<PayAccountProto>): ResponseEntity<Response> {
        protos.forEach { it.id ?: throw HasNullProtoException("id") }
        return success(service.delPayAccount(protos))
    }

    @GetMapping("/pay/account")
    fun getPayAccount(): ResponseEntity<Response> {
        return success(service.getPayAccount())
    }

}

@Service
class PayAccountService(val repo: PayAccountRepo) {

    fun newPayAccount(proto: PayAccountProto): PayAccount {
        repo.findByIdentifier(proto.identifier!!)?.let {
            throw AlreadyExistsEntityException(proto.identifier, PayAccount::class)
        }
        return updateOrCreatePayAccount(proto, null)
    }

    fun updatePayAccount(proto: PayAccountProto): PayAccount {
        val found = repo.findByIdOrThrow(proto.id!!)
        return updateOrCreatePayAccount(proto, found)
    }

    fun delPayAccount(protos: List<PayAccountProto>): List<PayAccountProto> {
        val filtered = protos.filter { it.id != null }
        repo.deleteAllById(filtered.map { it.id })
        return filtered
    }

    fun getPayAccount(): List<PayAccount> {
        return repo.findAll()
    }

    @Throws(ServiceException::class)
    private fun updateOrCreatePayAccount(proto: PayAccountProto, account: PayAccount?): PayAccount {
        val obj = (account ?: PayAccount()).apply {
            identifier = proto.identifier
            label = proto.label ?: identifier
            type = proto.type
            inUse = proto.inUse
        }
        return repo.save(obj)
    }
}

@Repository
interface PayAccountRepo : JpaRepository<PayAccount, Long> {
    fun findByIdentifier(identifier: String): PayAccount?
}

fun PayAccountRepo.findByIdentifierOrThrow(identifier: String): PayAccount =
    findByIdentifier(identifier) ?: throw NotFoundEntityException(identifier, getEntityTableName<PayAccount>())