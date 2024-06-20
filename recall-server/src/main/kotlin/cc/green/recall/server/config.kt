package cc.green.recall.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        return objectMapper
    }

    @Bean
    fun corsConfigurer(): CorsFilter {
        val config = CorsConfiguration()
        config.allowedOriginPatterns = listOf("*")
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.addAllowedHeader("*")
        config.addExposedHeader("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}