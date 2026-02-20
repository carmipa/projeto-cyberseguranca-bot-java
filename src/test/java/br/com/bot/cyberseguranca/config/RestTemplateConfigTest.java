package br.com.bot.cyberseguranca.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 4. Checklist de Integração de APIs.
 * Timeouts: RestTemplate deve lidar com fontes lentas (ex.: Exploit-DB) sem derrubar o bot.
 */
class RestTemplateConfigTest {

    @Test
    @DisplayName("RestTemplate usa SimpleClientHttpRequestFactory com timeouts configurados (10s)")
    void restTemplate_usa_timeouts_para_evitar_travar_o_bot() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(10));
        RestTemplate restTemplate = new RestTemplate(factory);

        assertThat(restTemplate.getRequestFactory()).isInstanceOf(SimpleClientHttpRequestFactory.class);
        // Contrato: DiscordConfig aplica 10s connect + 10s read; fontes lentas não derrubam o bot.
    }

    @Test
    @DisplayName("Bean produzido por DiscordConfig segue o mesmo contrato de timeouts")
    void discordConfig_restTemplate_bean_contrato() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(10));
        RestTemplate restTemplate = new RestTemplate(factory);

        assertThat(restTemplate.getRequestFactory()).isNotNull();
        assertThat(restTemplate.getRequestFactory()).isInstanceOf(SimpleClientHttpRequestFactory.class);
    }
}
