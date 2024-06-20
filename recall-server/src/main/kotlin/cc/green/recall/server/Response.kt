package cc.green.recall.server

import java.io.Serializable

class Response(val status: Int, val msg: String, val data: Any?, val error: Any? = null) : Serializable {

    companion object {
        fun success(data: Any?): Response {
            return Response(200, SUCCESS, data)
        }

        fun error(e: ServiceException): Response {
            return Response(e.statusCode, ERROR, null, ExceptionWrapper(e.reason, e.message))
        }

        fun unknownError(e: Exception): Response {
            return Response(4000, ERROR, null, ExceptionWrapper(e.stackTraceToString(), e.localizedMessage))
        }

        const val SUCCESS = "success"
        const val ERROR = "error"
    }

    class ExceptionWrapper(val reason: String?, val message: String?) : Serializable

}

