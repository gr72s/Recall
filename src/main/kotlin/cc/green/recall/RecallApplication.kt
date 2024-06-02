package cc.green.recall

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement
@EnableJpaRepositories
@SpringBootApplication
class RecallApplication

fun main(args: Array<String>) {
    runApplication<RecallApplication>(*args)
}
