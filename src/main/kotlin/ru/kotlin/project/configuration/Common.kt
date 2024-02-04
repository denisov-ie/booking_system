package ru.kotlin.project.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect
import ru.kotlin.project.filter.LoggerFilter


@Configuration
class Common {
    @Bean
    fun logger(): Logger {
        return LoggerFactory.getLogger(LoggerFilter::class.java)
    }

    @Bean
    fun securityDialect(): SpringSecurityDialect? {
        return SpringSecurityDialect()
    }
}