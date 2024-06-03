package cc.green.recall.services

import cc.green.recall.NotFoundEntityException
import cc.green.recall.Response
import jakarta.persistence.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.ResponseEntity
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations

inline fun <reified T, ID> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T = findById(id!!).orElseThrow {
    NotFoundEntityException(id.toString(), getEntityTableName<T>())
}

inline fun <reified T> getEntityTableName(): String {
    val tables = T::class.findAnnotations(Table::class)
    return if (tables.isEmpty()) T::class.simpleName!! else tables.first().name
}

fun getEntityTableName(kClass: KClass<*>): String {
    val tables = kClass.findAnnotations(Table::class)
    return if (tables.isEmpty()) kClass.simpleName!! else tables.first().name
}

fun success(data: Any?) =
    ResponseEntity.ok(Response.success(data))

