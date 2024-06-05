package cc.green.recall.server.services

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Common {

    @GetMapping("/ping")
    fun ping() = success("pong")

}