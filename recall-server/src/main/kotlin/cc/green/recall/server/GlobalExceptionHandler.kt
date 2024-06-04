package cc.green.recall.server

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(e: Exception): ResponseEntity<Response> {
        return when (e) {
            is ServiceException -> ResponseEntity.ok(Response.error(e))
            else -> ResponseEntity.ok(Response.unknownError())
        }
    }

}