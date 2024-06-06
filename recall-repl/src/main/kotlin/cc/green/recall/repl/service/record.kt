package cc.green.recall.repl.service

import cc.green.recall.server.Response
import cc.green.recall.server.services.ConsumeRecordProto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.math.BigDecimal
import java.time.LocalDate

@ShellComponent
class Record {
    @ShellMethod
    fun insert(
        sum: BigDecimal,
        consumePlatform: String,
        payPlatform: String,
        payAccount: String,
        @ShellOption consumeDate: String = LocalDate.now().toString()
    ): String {


        println("pre sum: ${sum}")
        val localDate = LocalDate.parse(consumeDate)
        println("pre consumeDate: $localDate")

        val consumePlatformWrapper = findConsumePlatform(consumePlatform) ?: return "not found $consumePlatform"
        println("found ${consumePlatformWrapper.id}, ${consumePlatformWrapper.label}")
        val payPlatformWrapper = findPayPlatform(payPlatform) ?: return "not found $payPlatform"
        println("found ${payPlatformWrapper.id}, ${payPlatformWrapper.label}")
        val payAccountWrapper = findPayAccount(payAccount) ?: return "not found $payAccount"
        println("found ${payAccountWrapper.id}, ${payAccountWrapper.label}")

        val consumeRecordProto = ConsumeRecordProto(
            null,
            payPlatformWrapper.id,
            sum,
            payAccountWrapper.id,
            localDate,
            consumePlatformWrapper.id,
            listOf()
        )
        val mapper = jacksonObjectMapper()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/consume/record/add")
            .post(
                mapper.writeValueAsString(consumeRecordProto)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            )
            .build()
        client.newCall(request).execute().use {

        }
        return ""
    }


    fun findConsumePlatform(identifier: String): Wrapper? {
        val mapper = jacksonObjectMapper()
        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/consume/platform").get().build()
        client.newCall(request).execute().use {
            val string = it.body?.string()
            val resValue = mapper.readValue(string, Response::class.java)
            return when (resValue.status) {
                200 -> {
                    (resValue.data as? List<*>)
                        ?.filter { proto ->
                            (proto as? Map<*, *>)?.get("identifier") == identifier
                        }?.firstNotNullOfOrNull {
                            val map = it as Map<*, *>
                            Wrapper(map["id"] as Long, map["identifier"] as String)
                        }
                }

                else -> null
            }
        }
    }

    fun findPayPlatform(identifier: String): Wrapper? {
        val mapper = jacksonObjectMapper()
        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/pay/platform").get().build()
        client.newCall(request).execute().use {
            val string = it.body?.string()
            val resValue = mapper.readValue(string, Response::class.java)
            return when (resValue.status) {
                200 -> {
                    (resValue.data as? List<*>)
                        ?.filter { proto ->
                            (proto as? Map<*, *>)?.get("identifier") == identifier
                        }?.firstNotNullOfOrNull {
                            val map = it as Map<*, *>
                            Wrapper(map["id"] as Long, map["identifier"] as String)
                        }
                }

                else -> null
            }
        }
    }

    fun findPayAccount(identifier: String): Wrapper? {
        val mapper = jacksonObjectMapper()
        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/pay/account").get().build()
        client.newCall(request).execute().use {
            val string = it.body?.string()
            val resValue = mapper.readValue(string, Response::class.java)
            return when (resValue.status) {
                200 -> {
                    (resValue.data as? List<*>)
                        ?.filter { proto ->
                            (proto as? Map<*, *>)?.get("identifier") == identifier
                        }?.firstNotNullOfOrNull {
                            val map = it as Map<*, *>
                            Wrapper(map["id"] as Long, map["identifier"] as String)
                        }
                }

                else -> null
            }
        }
    }

    data class Wrapper(val id: Long, val label: String)

}