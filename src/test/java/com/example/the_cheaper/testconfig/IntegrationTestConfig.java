package com.example.the_cheaper.testconfig;

import org.springframework.boot.test.context.TestConfiguration;

/**
 * Cấu hình chung cho Integration Test.
 * Có thể chứa các bean mock cho external services (như EmailService, S3Service)
 * để tránh call API thực tế khi chạy test.
 */
@TestConfiguration
public class IntegrationTestConfig {

}
