package com.example.the_cheaper.testconfig;

import com.example.the_cheaper.TheCheaperApplication;
import com.example.the_cheaper.external.EmailService;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/** Database tests must inherit this base so they never use the developer's database. */
@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest(classes = TheCheaperApplication.class)
@Import(IntegrationTestConfig.class)
public abstract class MySqlIntegrationTest {
    @MockitoBean
    protected EmailService emailService;
}
