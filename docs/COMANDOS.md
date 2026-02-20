# 📋 Tabela Completa de Comandos do Bot

Referência de todos os **Slash Commands** do **CyberSecurity & GRC Bot**, com descrição, permissão e parâmetros.

---

## 🎯 Visão geral

| Categoria | Comandos |
|-----------|----------|
| **Informação / Status** | `ping`, `about`, `dashboard`, `feeds`, `status` |
| **Inteligência e Varredura** | `force_scan`, `cve`, `scan` |
| **Configuração e GRC** | `check`, `add`, `delete`, `set_channel` |
| **Segurança / Honeypot** | `admin_panel` |

---

## 📊 Tabela de Comandos

| Comando | Descrição | Permissão | Parâmetros | Resposta / Comportamento |
|---------|-----------|-----------|------------|---------------------------|
| **`/ping`** | Verifica a latência do bot em relação ao Discord. | Qualquer usuário | — | Exibe latência em ms (mensagem efêmera). |
| **`/about`** | Informações do bot, desenvolvedor e stack. | Qualquer usuário | — | Nome **CyberIntel - NetRunner v1.0**, desenvolvedor, Java 25, Spring Boot, JDA, Docker. |
| **`/dashboard`** | Link e status do SOC Dashboard (Node-RED). | Qualquer usuário | — | Status [ONLINE] e link `http://seu-ip:1880/ui` (efêmera). |
| **`/feeds`** | Lista as 15 fontes de inteligência ativas (RSS/APIs). | Qualquer usuário | — | Lista os nomes das fontes carregadas de `sources.json`. |
| **`/status`** | Uptime e performance da JVM e varreduras. | Qualquer usuário | — | JVM Java 25 (ZGC), varreduras ativas (30 min), status das APIs (efêmera). |
| **`/force_scan`** | Dispara varredura manual nas 15 fontes de inteligência. | **Administrador** | — | Mensagem de confirmação e execução do ciclo (CISA, Ransomware.live, RSS). |
| **`/cve`** | Consulta CVE no NIST NVD. | Qualquer usuário | `cve_id` (obrigatório), ex: `CVE-2024-1234` | Mensagem indicando consulta em andamento. |
| **`/scan`** | Análise de URL (URLScan / VirusTotal). | Qualquer usuário | `url` (obrigatório) | Mensagem indicando análise em andamento. |
| **`/check`** | Realiza a auditoria de vulnerabilidades GRC. | Qualquer usuário | — | Lista vulnerabilidades da base ou "Ambiente em conformidade." |
| **`/add`** | Registra uma nova vulnerabilidade na base. | **Administrador** | `id`, `titulo`, `severidade`, `descricao` (todos obrigatórios) | Confirmação de inserção ou erro de persistência. |
| **`/delete`** | Remove uma vulnerabilidade da base pelo ID. | *Em implementação* | `id` (obrigatório) | *Em fase de implementação (NetRunner Migration).* |
| **`/set_channel`** | Fixa o canal operacional para alertas. | **Administrador** | — | Confirmação "Canal operacional fixado com sucesso!". |
| **`/admin_panel`** | Painel administrativo (honeypot). | **Owner** (ID configurado) | — | **Owner:** "Bem-vindo, Carminati. Painel administrativo liberado." — **Outros:** "Acesso Negado. Esta tentativa foi registrada na trilha de auditoria." |

---

## 🔐 Permissões e restrições

- **Qualquer usuário:** `ping`, `about`, `dashboard`, `feeds`, `status`, `cve`, `scan`, `check`.
- **Administrador do servidor (permissão Discord):** `force_scan`, `add`, `set_channel`.
- **Owner do bot (`discord.owner.id`):** acesso liberado ao `/admin_panel`. Qualquer outro usuário recebe "Acesso Negado" e a tentativa é logada (honeypot).

---

## 📁 Fontes de dados

- **Vulnerabilidades:** persistidas em `data/vulnerabilidades.json`.
- **Fontes de inteligência:** configuradas em `data/sources.json` (RSS, APIs, etc.).

Para mais detalhes de configuração, veja [INSTALACAO-E-CONFIGURACAO.md](INSTALACAO-E-CONFIGURACAO.md).
