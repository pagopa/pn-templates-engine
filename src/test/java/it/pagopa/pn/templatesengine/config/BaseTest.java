package it.pagopa.pn.templatesengine.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles()
@AutoConfigureMockMvc
public abstract class BaseTest {

    @Slf4j
    @SpringBootTest
    @EnableAutoConfiguration
    @ActiveProfiles
    public static class WithMockServer {

        @BeforeEach
        public void init() {
            log.info(this.getClass().getSimpleName());
        }
    }


}
