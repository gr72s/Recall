package cc.green.recall.repl.service

import cc.green.recall.server.Response
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class Display {

    @ShellMethod
    fun display(kind: String): String {
        val mapper = jacksonObjectMapper()
        val request = Request.Builder()
            .url("http://localhost:8080${getKind(kind)}")
            .get()
            .build()
        OkHttpClient().newCall(request).execute().use {
            val response =
                mapper.readValue(it.body?.string(), object : TypeReference<Response>() {})

            return when (response.msg) {
                Response.SUCCESS -> {
                    val s = StringBuilder()
                    (response.data as List<*>).forEach { tag ->
                        s.append("${tag.toString()}\n")
                    }
                    s.toString()
                }

                Response.ERROR -> response.error.toString()
                else -> "unknown error"
            }
        }
    }

    private fun getKind(kind: String): String =
        when (kind) {
            TAG -> TAG_URL
            PLATFORM -> PLATFORM_URL
            else -> ""
        }

    companion object {
        const val TAG = "tag"
        const val TAG_URL = "/consume/tag"
        const val PLATFORM = "platform"
        const val PLATFORM_URL = "/consume/platform"
    }

}