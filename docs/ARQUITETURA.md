# 📐 Arquitetura do CyberSecurity & GRC Bot

Documentação da arquitetura do bot, fluxos de dados e diagramas para exibição no GitHub.

---

## 🏗️ Visão geral

O bot é uma aplicação **Spring Boot 4** que consome a **Discord API** via **JDA**, executa varreduras em fontes de inteligência (CISA, Ransomware.live, RSS) e persiste alertas em **JSON** (YAGNI). O processamento paralelo usa **parallelStream()** nas 15 fontes, com persistência **idempotente** e **thread-safe**.

---

## 📐 Diagrama de arquitetura de alto nível

```mermaid
flowchart TB
    subgraph externo["🌐 Externo"]
        Discord[("Discord API")]
        Feeds[("Feeds de Segurança / Animes")]
    end

    subgraph app["🖥️ Aplicação Spring Boot 4"]
        Listener["📥 CommandListener\nSlash Commands & Eventos"]
        Service["⚙️ ThreatIntelligenceService\nLógica GRC & Varredura"]
        VulnSvc["🔒 VulnerabilityService\nPersistência Idempotente"]
        Scanner["🔍 CISA / Ransomware.live\nAPIs"]
        Monitor["📰 RSS Feeds\n(parallelStream)"]
    end

    subgraph persistencia["💾 Persistência"]
        JSON[("vulnerabilidades.json")]
        Sources[("sources.json")]
    end

    Discord --> Listener
    Listener --> Service
    Listener --> VulnSvc
    Service --> Scanner
    Service --> Monitor
    Service --> VulnSvc
    VulnSvc --> JSON
    Sources --> Service
    Feeds --> Monitor
```

---

## 🔄 Fluxo do comando `/force_scan`

```mermaid
sequenceDiagram
    participant U as Usuário (Admin)
    participant D as Discord
    participant L as CommandListener
    participant T as ThreatIntelligenceService
    participant V as VulnerabilityService
    participant API as APIs / RSS

    U->>D: /force_scan
    D->>L: onSlashCommandInteraction
    L->>L: validarAdmin()
    L->>D: reply "Disparando varredura..."
    L->>T: executarCicloOperacional()
    T->>API: monitorarApis() + processarFeedRss (parallelStream)
    API-->>T: dados
    T->>T: registrarSeNovo(id, titulo, ...)
    T->>V: listarVulnerabilidades() / adicionarVulnerabilidade()
    V->>V: sincronizado, idempotente
    V-->>T: ok
    T->>D: enviarNotificacao() (canal configurado)
```

---

## 🔐 Fluxo do Honeypot (`/admin_panel`)

```mermaid
flowchart LR
    A["/admin_panel"] --> B{user.id == ownerId?}
    B -->|Sim| C["🔓 Painel liberado"]
    B -->|Não| D["❌ Acesso Negado"]
    D --> E["Log [HONEYPOT] no console"]
```

O `ownerId` é configurado via `discord.owner.id` (env ou `application.properties`). Qualquer outra conta recebe "Acesso Negado" e a tentativa é registrada no stderr para auditoria.

---

## 📂 Estrutura de pacotes (Java)

```mermaid
flowchart LR
    subgraph br.com.bot.cyberseguranca
        config["config\nDiscordConfig"]
        listener["listener\nCommandListener"]
        model["model\nAlertaSeguranca, ConfigSources, RssSource..."]
        service["service\nVulnerabilityService\nThreatIntelligenceService\nBotConfigService"]
        exception["exception\nPersistenceException\nBotBaseException..."]
    end
    config --> listener
    listener --> service
    service --> model
    service --> exception
```

---

## 🛠️ Stack técnica resumida

| Camada | Tecnologia |
|--------|------------|
| **Linguagem** | Java 25 (LTS) |
| **Framework** | Spring Boot 4.0.3 |
| **Build** | Gradle (Kotlin DSL) |
| **Discord** | JDA (Java Discord API) |
| **HTTP** | RestTemplate (timeouts 10s) |
| **Persistência** | JSON (Jackson), arquivos em `data/` |
| **RSS** | Rome (SyndFeed) |
| **Container** | Docker, Jib |

---

## 📚 Documentos relacionados

- [COMANDOS.md](COMANDOS.md) — Tabela completa de comandos do bot
- [INSTALACAO-E-CONFIGURACAO.md](INSTALACAO-E-CONFIGURACAO.md) — Setup e variáveis de ambiente
- [TESTES-MANUAL-E-INTEGRACAO.md](TESTES-MANUAL-E-INTEGRACAO.md) — Checklist de testes manuais e integração
