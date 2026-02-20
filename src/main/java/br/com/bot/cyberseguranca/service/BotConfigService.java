package br.com.bot.cyberseguranca.service;

import br.com.bot.cyberseguranca.exception.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BotConfigService {

    @Value("${DISCORD_TOKEN}")
    private String token;

    @Value("${DISCORD_CHANNEL_ID}")
    private String channelId;

    public String getToken() {
        if (token == null || token.isBlank() || token.equals("seu_token_aqui")) {
            throw new ConfigurationException("DISCORD_TOKEN inválido ou ausente no .env");
        }
        return token;
    }

    public String getChannelId() {
        if (channelId == null || channelId.isBlank()) {
            throw new ConfigurationException("DISCORD_CHANNEL_ID não configurado no .env");
        }
        return channelId;
    }
}