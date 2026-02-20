package br.com.bot.cyberseguranca.config;

import br.com.bot.cyberseguranca.exception.ConfigurationException;
import br.com.bot.cyberseguranca.service.BotConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executors;

@Configuration
public class DiscordConfig {

    @Bean
    public JDA jda(BotConfigService configService) {
        try {
            // Java 25: Otimização com Virtual Threads para performance máxima
            var executor = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name("jda-virtual-thread-", 0).factory()
            );

            return JDABuilder.createDefault(configService.getToken())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    .setEventPool(executor)
                    .build();

        } catch (ConfigurationException e) {
            // Em Cybersecurity, falhas de config são críticas
            System.err.println("FATAL: Falha na inicialização do Bot - " + e.getMessage());
            throw e;
        }
    }
}