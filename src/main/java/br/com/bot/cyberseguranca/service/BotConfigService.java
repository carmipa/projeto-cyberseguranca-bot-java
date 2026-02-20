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

    /**
     * Recupera o Token com validação de segurança.
     * Em conformidade com GRC, impede a inicialização sem credenciais.
     */
    public String getToken() {
        if (token == null || token.isBlank() || token.contains("seu_token")) {
            throw new ConfigurationException("DISCORD_TOKEN ausente ou inválido nas variáveis de ambiente.");
        }
        return token;
    }

    /**
     * Recupera o ID do canal de auditoria.
     */
    public String getChannelId() {
        if (channelId == null || channelId.isBlank()) {
            throw new ConfigurationException("DISCORD_CHANNEL_ID não configurado. O bot não saberá onde reportar.");
        }
        return channelId;
    }
}