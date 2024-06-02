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

data class PayMethodProto(
    val id: Long?,
    val identifier: String?,
    val label: String?
)

@RestController
class PayMethodController(val service: PayMethodService) {

    @PostMapping("/pay/method/add")
    fun addPayMethod(@RequestBody paymentProto: PayMethodProto): ResponseEntity<Response> {
        paymentProto.identifier ?: throw HasNullProtoException("identifier")
        return success(service.newPayMethod(paymentProto))
    }

    @PostMapping("/pay/method/update")
    fun updatePayMethod(@RequestBody paymentProto: PayMethodProto): ResponseEntity<Response> {
        paymentProto.id ?: throw HasNullProtoException("id")
        return success(service.updatePayMethod(paymentProto))
    }

    @PostMapping("/pay/method/del")
    fun delPayMethod(@RequestBody protos: List<PayMethodProto>): ResponseEntity<Response> {
        protos.forEach { it.id ?: throw HasNullProtoException("id") }
        return success(service.delPayMethod(protos))
    }

    @GetMapping("/pay/method")
    fun getPayMethod(): ResponseEntity<Response> {
        return success(service.getPayMethod())
    }

}

@Service
class PayMethodService(val repo: PayMethodRepo) {

    fun newPayMethod(proto: PayMethodProto): PayMethod {
        repo.findByIdentifier(proto.identifier!!)?.let {
            throw AlreadyExistsEntityException(proto.identifier, PayMethod::class)
        }
        return updateOrCreatePayMethod(proto, null)
    }

    fun updatePayMethod(paymentProto: PayMethodProto): PayMethod {
        val foundPayMethod = repo.findByIdOrThrow(paymentProto.id!!)
        return updateOrCreatePayMethod(paymentProto, foundPayMethod)
    }

    fun delPayMethod(protos: List<PayMethodProto>): List<PayMethodProto> {
        val filtered = protos.filter { it.id != null }
        repo.deleteAllById(filtered.map { it.id })
        return filtered
    }

    fun getPayMethod(): List<PayMethod> {
        return repo.findAll()
    }

    @Throws(ServiceException::class)
    private fun updateOrCreatePayMethod(paymentProto: PayMethodProto, payment: PayMethod?): PayMethod {
        val obj = (payment ?: PayMethod()).apply {
            identifier = paymentProto.identifier
        }
        return repo.save(obj)
    }
}

@Repository
interface PayMethodRepo : JpaRepository<PayMethod, Long> {
    fun findByIdentifier(identifier: String): PayMethod?
}

fun PayMethodRepo.findByIdentifierOrThrow(identifier: String): PayMethod =
    findByIdentifier(identifier) ?: throw NotFoundEntityException(identifier, getEntityTableName<PayMethod>())