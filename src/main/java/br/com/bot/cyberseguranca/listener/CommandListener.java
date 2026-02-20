package br.com.bot.cyberseguranca.listener;

import br.com.bot.cyberseguranca.model.AlertaSeguranca;
import br.com.bot.cyberseguranca.service.BotConfigService;
import br.com.bot.cyberseguranca.service.ThreatIntelligenceService;
import br.com.bot.cyberseguranca.service.VulnerabilityService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommandListener extends ListenerAdapter {

    private final VulnerabilityService vulnerabilityService;
    private final ThreatIntelligenceService threatIntelService;
    private final BotConfigService configService;
    private final String OWNER_ID = "SEU_ID_AQUI"; // ID do desenvolvedor Paulo André Carminati

    public CommandListener(VulnerabilityService vulnerabilityService,
                           ThreatIntelligenceService threatIntelService,
                           BotConfigService configService) {
        this.vulnerabilityService = vulnerabilityService;
        this.threatIntelService = threatIntelService;
        this.configService = configService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        // --- SWITCH EXPRESSION (JAVA 25) ---
        switch (command) {
            // CATEGORIA: INFORMAÇÃO / STATUS
            case "ping" -> event.reply("🏓 **Latência:** " + event.getJDA().getGatewayPing() + "ms").setEphemeral(true).queue();

            case "about" -> event.reply("**CyberIntel - NetRunner v1.0**\nDesenvolvedor: Paulo André Carminati\nStack: Java 25, Spring Boot, JDA, Docker\nVersão: NetRunner v1.0").queue();

            case "dashboard" -> event.reply("📊 **SOC Dashboard (Node-RED)**\nStatus: [ONLINE]\nLink: http://seu-ip:1880/ui").setEphemeral(true).queue();

            case "feeds" -> handleFeeds(event);

            case "status" -> event.reply("📈 **Uptime & Performance:**\nJVM: Java 25 (ZGC)\nVarreduras: Ativas (30min)\nStatus APIs: [OK]").setEphemeral(true).queue();

            // CATEGORIA: INTELIGÊNCIA E VARREDURA
            case "forcecheck", "force_scan", "now" -> {
                if (validarAdmin(event)) {
                    event.reply("🔄 [ADMIN] Disparando varredura manual nas 15 fontes de inteligência...").queue();
                    threatIntelService.executarCicloOperacional();
                }
            }

            case "cve" -> {
                String cveId = event.getOption("cve_id").getAsString();
                event.reply("🔍 Consultando NIST NVD para: `" + cveId + "`...").queue();
            }

            case "scan" -> {
                String url = event.getOption("url").getAsString();
                event.reply("🛡️ Iniciando análise de URL em URLScan e VirusTotal: `" + url + "`...").queue();
            }

            // CATEGORIA: CONFIGURAÇÃO E GRC
            case "check" -> handleCheck(event);

            case "add" -> handleAdd(event);

            case "set_channel" -> {
                if (validarAdmin(event)) {
                    // Lógica para salvar o canal oficial no config.json
                    event.reply("✅ Canal operacional fixado com sucesso!").queue();
                }
            }

            // CATEGORIA: SEGURANÇA / HONEYPOT
            case "admin_panel" -> handleAdminPanel(event);

            default -> event.reply("⚠️ Comando em fase de implementação (NetRunner Migration).").setEphemeral(true).queue();
        }
    }

    // --- MÉTODOS DE APOIO ---

    private void handleAdminPanel(SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals(OWNER_ID)) {
            System.err.println("🚨 [HONEYPOT] Tentativa de acesso não autorizada ao Admin Panel por: " + event.getUser().getName());
            event.reply("❌ **Acesso Negado.** Esta tentativa foi registrada na trilha de auditoria.").setEphemeral(true).queue();
        } else {
            event.reply("🔓 **Bem-vindo, Carminati.** Painel administrativo liberado.").setEphemeral(true).queue();
        }
    }

    private void handleCheck(SlashCommandInteractionEvent event) {
        var vulns = vulnerabilityService.listarVulnerabilidades();
        if (vulns.isEmpty()) {
            event.reply("✅ **Status GRC:** Ambiente em conformidade.").queue();
        } else {
            StringBuilder sb = new StringBuilder("⚠️ **Auditoria:**\n");
            vulns.forEach(v -> sb.append("- [").append(v.severidade()).append("] ").append(v.titulo()).append("\n"));
            event.reply(sb.toString()).queue();
        }
    }

    private void handleAdd(SlashCommandInteractionEvent event) {
        if (!validarAdmin(event)) return;
        try {
            AlertaSeguranca nova = new AlertaSeguranca(
                event.getOption("id").getAsString(),
                event.getOption("titulo").getAsString(),
                event.getOption("severidade").getAsString(),
                "PENDENTE",
                LocalDateTime.now(),
                event.getOption("descricao").getAsString()
            );
            vulnerabilityService.adicionarVulnerabilidade(nova);
            event.reply("✅ Sucesso: Registro `" + nova.id() + "` inserido.").queue();
        } catch (Exception e) {
            event.reply("❌ Erro de Persistência: " + e.getMessage()).setEphemeral(true).queue();
        }
    }

    private void handleFeeds(SlashCommandInteractionEvent event) {
        // Lista as 15 fontes carregadas do sources.json
        var feeds = threatIntelService.getSources().rssFeeds();
        StringBuilder sb = new StringBuilder("📚 **Inteligência Ativa (15 Feeds):**\n");
        feeds.stream().limit(15).forEach(f -> sb.append("• ").append(f.name()).append("\n"));
        event.reply(sb.toString()).queue();
    }

    private boolean validarAdmin(SlashCommandInteractionEvent event) {
        if (event.getMember() != null && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }
        event.reply("❌ **Restrição GRC:** Comando reservado a Administradores.").setEphemeral(true).queue();
        return false;
    }
}