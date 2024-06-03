package cc.green.recall.repl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecallReplApplication

fun main(args: Array<String>) {
    runApplication<RecallReplApplication>(*args)
}