package cc.green.recall

import cc.green.recall.services.getEntityTableName
import kotlin.reflect.KClass

abstract class ServiceException(val statusCode: Int, val reason: String, message: String) : RuntimeException(message)

class NotFoundEntityException(identifier: String, tableName: String) :
    ServiceException(4001, REASON, "$identifier on entity $tableName not found") {
    companion object {
        const val REASON = "NotFoundEntity"
    }
}

class AlreadyExistsEntityException(identifier: String, tableKClass: KClass<*>) :
    ServiceException(40001, REASON, "$identifier with table ${getEntityTableName(tableKClass)} already exists") {
    companion object {
        const val REASON = "AlreadyExistsEntity"
    }
}

class HasNullProtoException(param: String) :
    ServiceException(4001, REASON, "$param is null") {
    companion object {
        const val REASON = "ProtoHasNull"
    }
}