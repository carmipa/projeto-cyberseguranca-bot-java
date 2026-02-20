package br.com.bot.cyberseguranca.service;

import br.com.bot.cyberseguranca.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

@Service
public class ThreatIntelligenceService {

    private final VulnerabilityService vulnerabilityService;
    private final BotConfigService configService;
    private final JDA jda;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private ConfigSources sources;

    /**
     * @Lazy JDA: Quebra o ciclo de dependência circular na inicialização do Spring Context.
     */
    public ThreatIntelligenceService(VulnerabilityService vulnerabilityService,
                                     BotConfigService configService,
                                     @Lazy JDA jda,
                                     ObjectMapper objectMapper,
                                     RestTemplate restTemplate) {
        this.vulnerabilityService = vulnerabilityService;
        this.configService = configService;
        this.jda = jda;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void carregarFontes() {
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "sources.json");
            if (file.exists()) {
                this.sources = objectMapper.readValue(file, ConfigSources.class);
                System.out.println("✅ [GRC-INTEL] Motor de Inteligência iniciado. Feeds: " + sources.rssFeeds().size());
            }
        } catch (IOException e) {
            System.err.println("❌ [ERRO] Falha ao carregar sources.json: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 1800000)
    public void executarCicloOperacional() {
        if (sources == null) return;
        System.out.println("🔄 [AUDITORIA] Iniciando varredura massiva... " + LocalDateTime.now());

        monitorarApis();
        sources.rssFeeds().parallelStream().forEach(this::processarFeedRss);
    }

    private void monitorarApis() {
        // CISA KEV Monitor
        try {
            String urlCisa = "https://www.cisa.gov/sites/default/files/feeds/known_exploited_vulnerabilities.json";
            JsonNode root = restTemplate.getForObject(urlCisa, JsonNode.class);
            JsonNode latest = root.path("vulnerabilities").get(0);
            if (latest != null) {
                registrarSeNovo(latest.path("cveID").asText(), latest.path("vulnerabilityName").asText(), "CRITICAL", latest.path("shortDescription").asText());
            }
        } catch (Exception e) { System.err.println("⚠️ [CISA] Erro: " + e.getMessage()); }

        // Ransomware.live Monitor
        try {
            String urlRansom = "https://api.ransomware.live/v2/recentvictims";
            JsonNode victims = restTemplate.getForObject(urlRansom, JsonNode.class);
            if (victims != null && victims.isArray() && !victims.isEmpty()) {
                JsonNode latest = victims.get(0);
                registrarSeNovo("RANSOM-" + Math.abs(latest.path("victim").asText().hashCode()),
                                "Alerta Ransomware: " + latest.path("victim").asText(),
                                "HIGH", "Grupo: " + latest.path("group_name").asText());
            }
        } catch (Exception e) { System.err.println("⚠️ [RANSOM] Erro: " + e.getMessage()); }
    }

    private void processarFeedRss(RssSource source) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            URL feedUrl = URI.create(source.url()).toURL();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            if (!feed.getEntries().isEmpty()) {
                SyndEntry entry = feed.getEntries().get(0);
                String idUnico = "RSS-" + Math.abs(entry.getLink().hashCode());

                registrarSeNovo(idUnico,
                                "[" + source.category() + "] " + entry.getTitle(),
                                source.priority(),
                                entry.getLink());
            }
        } catch (Exception e) {
            System.err.println("⚠️ [RSS-FAIL] " + source.name() + ": " + e.getMessage());
        }
    }

    private void registrarSeNovo(String id, String titulo, String severidade, String desc) {
        if (vulnerabilityService.listarVulnerabilidades().stream().noneMatch(v -> v.id().equals(id))) {
            AlertaSeguranca nova = new AlertaSeguranca(id, titulo, severidade, "NEW", LocalDateTime.now(), desc);
            vulnerabilityService.adicionarVulnerabilidade(nova);
            enviarNotificacao(nova);
        } else {
            System.out.println("📋 [IDEMPOTÊNCIA] Registros já existem em vulnerabilidades.json para id: " + id);
        }
    }

    private void enviarNotificacao(AlertaSeguranca alerta) {
        try {
            TextChannel channel = jda.getTextChannelById(configService.getChannelId());
            if (channel != null) {
                String header = (alerta.severidade().equalsIgnoreCase("CRITICAL") || alerta.severidade().equalsIgnoreCase("HIGH"))
                                ? "🚨 **ALERTA CRÍTICO** 🚨" : "🔔 **Informativo Cyber**";

                channel.sendMessage(header + "\n**ID:** " + alerta.id() + "\n**Título:** " + alerta.titulo() + "\n**Link:** " + alerta.descricao()).queue();
            }
        } catch (Exception e) {
            System.err.println("⚠️ [DISCORD] Falha ao enviar notificação: " + e.getMessage());
        }
    }

    public ConfigSources getSources() {
        return this.sources;
    }
}