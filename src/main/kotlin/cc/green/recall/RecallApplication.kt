package cc.green.recall

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecallApplication

fun main(args: Array<String>) {
    runApplication<RecallApplication>(*args)
}
