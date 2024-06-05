package cc.green.recall.repl.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class Connect {

    @ShellMethod
    fun ping(url: String = "localhost", port: Int = 8080): Any? {
        val request = Request.Builder()
            .url("http://${url}:${port}/ping")
            .get()
            .build()
        OkHttpClient().newCall(request).execute().use {
            val mapper = jacksonObjectMapper()
            val readValue = mapper.readValue(it.body?.string(), object : TypeReference<MutableMap<String, Any>>() {})
            return readValue.toString()
        }
    }
}
