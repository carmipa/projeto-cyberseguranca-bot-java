package br.com.bot.cyberseguranca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "DISCORD_TOKEN=test-token-context",
        "DISCORD_CHANNEL_ID=123456789"
})
@SpringBootTest
class ProjetoCybersegurancaBotJavaApplicationTests {

    @Test
    void contextLoads() {
    }

    @Configuration
    static class TestConfig {
        @Bean
        ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        }

        @Bean
        RestTemplate restTemplate() {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Duration.ofSeconds(10));
            factory.setReadTimeout(Duration.ofSeconds(10));
            return new RestTemplate(factory);
        }

        @Bean
        JDA jda() {
            return mock(JDA.class);
        }
    }
}
