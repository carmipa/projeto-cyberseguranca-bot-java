package br.com.bot.cyberseguranca.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 2. Teste de Concorrência (Parallel Stream).
 * /feeds (getSources) deve responder mesmo enquanto o ciclo @Scheduled ou /force_scan está rodando.
 * Java 25 deve gerenciar as threads sem travar a resposta do Slash Command.
 */
@ExtendWith(MockitoExtension.class)
class ThreatIntelligenceConcurrencyTest {

    @Mock
    private net.dv8tion.jda.api.JDA jda;

    @Test
    @DisplayName("getSources() responde sem travar enquanto executarCicloOperacional roda em paralelo")
    void feeds_respondem_durante_ciclo_operacional() throws InterruptedException {
        ThreatIntelligenceService service = createServiceWithNullSources();

        CountDownLatch cycleStarted = new CountDownLatch(1);
        CountDownLatch feedsDone = new CountDownLatch(1);
        AtomicReference<Exception> feedsError = new AtomicReference<>();

        Thread cycleThread = new Thread(() -> {
            cycleStarted.countDown();
            service.executarCicloOperacional();
        });

        Thread feedsThread = new Thread(() -> {
            try {
                cycleStarted.await(2, TimeUnit.SECONDS);
                var sources = service.getSources();
                assertThat(Thread.currentThread().isAlive()).isTrue();
            } catch (Exception e) {
                feedsError.set(e);
            } finally {
                feedsDone.countDown();
            }
        });

        cycleThread.start();
        feedsThread.start();
        boolean feedsCompleted = feedsDone.await(5, TimeUnit.SECONDS);
        cycleThread.join(5_000);

        assertThat(feedsCompleted).isTrue();
        assertThat(feedsError.get()).isNull();
    }

    private ThreatIntelligenceService createServiceWithNullSources() {
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            var vs = new VulnerabilityService(mapper,
                    java.nio.file.Files.createTempDirectory("cyber").resolve("vuln.json").toString());
            var config = org.mockito.Mockito.mock(BotConfigService.class);
            var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(java.time.Duration.ofSeconds(10));
            factory.setReadTimeout(java.time.Duration.ofSeconds(10));
            var restTemplate = new org.springframework.web.client.RestTemplate(factory);
            var service = new ThreatIntelligenceService(vs, config, jda, mapper, restTemplate);
            return service;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
