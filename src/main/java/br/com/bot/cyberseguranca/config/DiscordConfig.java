package br.com.bot.cyberseguranca.config;

import br.com.bot.cyberseguranca.listener.CommandListener;
import br.com.bot.cyberseguranca.service.BotConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DiscordConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Configuração do RestTemplate com Timeouts (GRC Best Practices)
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Timeout de conexão (10 segundos)
        factory.setConnectTimeout(10000);

        // Timeout de leitura de dados (10 segundos)
        factory.setReadTimeout(10000);

        return new RestTemplate(factory);
    }

    @Bean
    public JDA jda(BotConfigService configService, CommandListener commandListener) {
        JDA jda = JDABuilder.createDefault(configService.getToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(commandListener)
                .build();

        // Registro dos Slash Commands
        jda.updateCommands().addCommands(
                Commands.slash("check", "Realiza a auditoria de vulnerabilidades GRC"),
                Commands.slash("add", "Registra uma nova vulnerabilidade")
                        .addOption(OptionType.STRING, "id", "ID da vulnerabilidade", true)
                        .addOption(OptionType.STRING, "titulo", "Título do alerta", true)
                        .addOption(OptionType.STRING, "severidade", "CRITICA, ALTA, MEDIA", true)
                        .addOption(OptionType.STRING, "descricao", "Detalhes técnicos", true),
                Commands.slash("delete", "Remove uma vulnerabilidade da base")
                        .addOption(OptionType.STRING, "id", "ID para remoção", true),
                Commands.slash("ping", "Verifica latência do bot"),
                Commands.slash("about", "Informações do bot"),
                Commands.slash("dashboard", "Link do SOC Dashboard"),
                Commands.slash("feeds", "Lista as 15 fontes de inteligência ativas"),
                Commands.slash("status", "Uptime e status das APIs"),
                Commands.slash("force_scan", "[ADMIN] Dispara varredura manual nas fontes"),
                Commands.slash("cve", "Consulta CVE no NIST NVD").addOption(OptionType.STRING, "cve_id", "Ex: CVE-2024-1234", true),
                Commands.slash("scan", "Análise de URL (URLScan/VirusTotal)").addOption(OptionType.STRING, "url", "URL a analisar", true),
                Commands.slash("set_channel", "[ADMIN] Fixa o canal operacional"),
                Commands.slash("admin_panel", "Painel administrativo (honeypot)")
        ).queue();

        return jda;
    }
}