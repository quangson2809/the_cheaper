package com.example.the_cheaper.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

/**
 * Spring owns the container lifecycle, including cached application contexts.
 * ServiceConnection overrides the application's datasource connection details.
 */
@TestConfiguration(proxyBeanMethods = false)
public class IntegrationTestConfig {
    @Bean
    @ServiceConnection
    MySQLContainer mysql() {
        return new MySQLContainer("mysql:8.4")
                .withDatabaseName("the_cheaper_test");
    }
}
