package com.katsu.booklibrary.support

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@ActiveProfiles("test")
abstract class PostgresContainerBase {
    companion object {
        @JvmStatic
        protected val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:17-alpine")
                .withDatabaseName("booklibrary_test")
                .withUsername("test")
                .withPassword("test")
                .withCommand("postgres", "-c", "timezone=Asia/Tokyo")
                .also { it.start() }

        @JvmStatic
        @DynamicPropertySource
        fun props(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }
}
