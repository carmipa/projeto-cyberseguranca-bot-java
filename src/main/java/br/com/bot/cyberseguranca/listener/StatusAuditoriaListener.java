package br.com.bot.cyberseguranca.listener;

import br.com.bot.cyberseguranca.service.BotConfigService;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class StatusAuditoriaListener extends ListenerAdapter {

    private final BotConfigService config;

    public StatusAuditoriaListener(BotConfigService config) {
        this.config = config;
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            var channel = event.getJDA().getTextChannelById(config.getChannelId());
            if (channel != null) {
                channel.sendMessage("🛡️ **Sistema de Auditoria GRC Java 25 Online**\n" +
                                   "Monitoramento ativo e persistência JSON configurada.").queue();
            }
        } catch (Exception e) {
            // Log de auditoria caso o canal ID seja válido mas o bot não tenha acesso
            System.err.println("Erro ao enviar mensagem de boas-vindas: " + e.getMessage());
        }
    }
}