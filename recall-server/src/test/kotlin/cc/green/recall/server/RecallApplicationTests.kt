package cc.green.recall.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.EnableTransactionManagement

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class RecallApplicationTests


@EnableTransactionManagement
@EnableJpaRepositories
@SpringBootApplication
@Profile("test")
class RecallApplication

fun main(args: Array<String>) {
    runApplication<RecallApplication>(*args)
}


