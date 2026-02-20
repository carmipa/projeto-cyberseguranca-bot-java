# 🚀 Instalação e Configuração

Guia para clonar, configurar e rodar o **CyberSecurity & GRC Bot** no GitHub e em ambiente local.

---

## 📋 Pré-requisitos

| Requisito | Versão / Observação |
|-----------|----------------------|
| **JDK** | 25 (recomendado para Virtual Threads e recursos LTS) |
| **Gradle** | Incluso via wrapper (`./gradlew`) |
| **Docker** | Opcional, para build de imagem com Jib |

---

## 🔧 Variáveis de ambiente

Configure no `.env` (ou no sistema / `application.properties`):

| Variável | Obrigatório | Descrição |
|----------|-------------|-----------|
| **`DISCORD_TOKEN`** | ✅ Sim | Token do bot no [Discord Developer Portal](https://discord.com/developers/applications). |
| **`DISCORD_CHANNEL_ID`** | ✅ Sim | ID do canal de texto onde o bot envia alertas (numérico). |
| **`discord.owner.id`** | ❌ Não | ID do usuário Discord dono do bot (para `/admin_panel` e honeypot). Padrão: `SEU_ID_AQUI`. |
| **`vulnerability.file.path`** | ❌ Não | Caminho completo do arquivo de vulnerabilidades. Padrão: `{user.dir}/data/vulnerabilidades.json`. |

### Exemplo de `.env`

```env
DISCORD_TOKEN=seu_token_aqui
DISCORD_CHANNEL_ID=1234567890123456789
discord.owner.id=9876543210987654321
```

---

## 📥 Clone e execução

### 1. Clone o repositório

```bash
git clone https://github.com/carmipa/projeto-cyberseguranca-bot-java.git
cd projeto-cyberseguranca-bot-java
```

### 2. Configure as variáveis

Crie um arquivo `.env` na raiz do projeto (ou exporte as variáveis) com `DISCORD_TOKEN` e `DISCORD_CHANNEL_ID`. Opcionalmente defina `discord.owner.id`.

### 3. Build e execução

**Local (Gradle):**

```bash
./gradlew bootRun
```

**Windows (PowerShell):**

```powershell
.\gradlew.bat bootRun
```

**Testes:**

```bash
./gradlew test
```

---

## 🐳 Docker e Jib

O projeto usa **Jib** para construir imagens Docker sem Dockerfile.

**Build da imagem:**

```bash
./gradlew jibDockerBuild
```

A imagem gerada é `cyberbot-carminati`. Para rodar com variáveis de ambiente:

```bash
docker run -e DISCORD_TOKEN=... -e DISCORD_CHANNEL_ID=... -e discord.owner.id=... cyberbot-carminati
```

**Docker Compose:** o Spring Boot 4 suporta Docker Compose; use `compose.yaml` na raiz se disponível, ajustando variáveis conforme necessário.

---

## 📁 Estrutura de dados (pastas `data/`)

| Arquivo | Descrição |
|---------|------------|
| **`data/vulnerabilidades.json`** | Lista de alertas de vulnerabilidade (GRC). Criado automaticamente na primeira gravação. |
| **`data/sources.json`** | Configuração das fontes de inteligência (RSS, APIs). Necessário para a varredura. |
| **`data/logs/`** | Logs da aplicação (se configurado no `logback-spring.xml`). |

A pasta `data/` costuma estar no `.gitignore`; não versione tokens nem dados sensíveis.

---

## 📚 Documentos relacionados

- [COMANDOS.md](COMANDOS.md) — Tabela de todos os comandos do bot
- [ARQUITETURA.md](ARQUITETURA.md) — Diagramas e fluxos
- [TESTES-MANUAL-E-INTEGRACAO.md](TESTES-MANUAL-E-INTEGRACAO.md) — Testes manuais e integração
