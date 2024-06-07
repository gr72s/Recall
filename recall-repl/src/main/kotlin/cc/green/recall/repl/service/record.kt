package cc.green.recall.repl.service

import cc.green.recall.server.Response
import cc.green.recall.server.services.ConsumePlatformProto
import cc.green.recall.server.services.ConsumeRecordProto
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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
class Insert {

    @ShellMethod
    fun insertConsumePlatform(
        identifier: String,
        @ShellOption(defaultValue = "") label: String
    ): String? {
        println(
            """
            identifier $identifier
            label $label
        """.trimIndent()
        )
        findConsumePlatform(identifier)?.let {
            return "already exist $identifier"
        } ?: println("$identifier not found")

        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        val reqBody = mapper
            .writeValueAsString(ConsumePlatformProto(null, identifier, label))
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/consume/platform/add")
            .post(reqBody)
            .build()
        return client.newCall(request).execute().use { response ->
            val resValue = mapper.readValue(response.body?.string(), Response::class.java)
            val map = resValue.data as? Map<*, *> ?: return "response error"
            "id=${map["id"]}, identifier=${map["identifier"]}, label=${map["label"]}"
        }
    }

    @ShellMethod
    fun insertRecord(
        sum: BigDecimal,
        consumePlatform: String,
        payPlatform: String,
        payAccount: String,
        @ShellOption consumeDate: String = LocalDate.now().toString()
    ): String? {
        println("pre sum: $sum")
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

        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        val valueAsString = mapper.writeValueAsString(consumeRecordProto)
        println(valueAsString)
        val postData = valueAsString.toRequestBody("application/json; charset=utf-8".toMediaType())

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/repl/consume/record/add")
            .post(postData)
            .build()
        return client.newCall(request).execute().use {
            val string = it.body?.string()
            val resValue = mapper.readValue(string, Response::class.java)
            when (resValue.status) {
                200 -> (resValue.data as? Map<*, *>).toString()
                else -> "error"
            }
        }
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
                            Wrapper((map["id"] as Int).toLong(), map["identifier"] as String)
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
                            Wrapper((map["id"] as Int).toLong(), map["identifier"] as String)
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
                            Wrapper((map["id"] as Int).toLong(), map["identifier"] as String)
                        }
                }

                else -> null
            }
        }
    }

    data class Wrapper(val id: Long, val label: String)

}