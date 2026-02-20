# Checklist de Testes Manuais e Integração

Este documento descreve os cenários de teste que exigem execução manual (Discord, APIs externas) e como validar encoding e timeouts.

---

## 1. Teste de Idempotência e Persistência (automático + manual)

### Automático (já coberto por testes)
- **VulnerabilityServiceTest**: adicionar o mesmo `id` duas vezes não duplica; JSON permanece válido; concorrência não corrompe o arquivo.

### Manual no Discord
1. **Configure** `discord.owner.id` no `application.properties` (ou env) com seu user ID do Discord.
2. **Dispare** `/force_scan` **duas vezes seguidas** (conta admin).
3. **Observe**:
   - **Primeira vez**: o bot posta as notícias no canal e grava em `data/vulnerabilidades.json`.
   - **Segunda vez**: no **console** deve aparecer a mensagem:  
     `📋 [IDEMPOTÊNCIA] Registros já existem em vulnerabilidades.json para id: ...`  
     (e/ou `📋 [IDEMPOTÊNCIA] Registro já existente em vulnerabilidades.json: ...`).
4. **Validação**: abra `data/vulnerabilidades.json` e confira que não há entradas duplicadas e que o JSON está bem formado (sem corrupção).

---

## 2. Teste de Concorrência (Parallel Stream)

### Automático (já coberto)
- **ThreatIntelligenceConcurrencyTest**: `getSources()` responde enquanto `executarCicloOperacional()` roda em paralelo, sem travar.

### Manual no Discord
1. **Dispare** `/force_scan` (ou aguarde o ciclo `@Scheduled` de 30 min).
2. **Enquanto** a varredura estiver rodando, execute **`/feeds`**.
3. **Observe**: o comando `/feeds` deve responder em tempo hábil com a lista das 15 fontes, sem travar. O Java 25 (virtual threads / pool) deve gerenciar as threads sem bloquear a resposta do Slash Command.

---

## 3. Validação do "Malandro Protocol" (Honeypot)

### Automático (já coberto)
- **CommandListenerAdminPanelTest**: com `OWNER_ID` libera acesso; com outra conta retorna "Acesso Negado" e o log de honeypot no stderr.

### Manual no Discord
1. **Configure** `discord.owner.id` com o ID da sua conta (ex.: `discord.owner.id=123456789012345678`).
2. **Com a conta owner**: use `/admin_panel` → deve aparecer: **"Bem-vindo, Carminati. Painel administrativo liberado."**
3. **Com outra conta** (ou outro servidor): use `/admin_panel` → deve aparecer: **"Acesso Negado. Esta tentativa foi registrada na trilha de auditoria."**
4. **No console** (IntelliJ/Cursor): deve aparecer uma linha de **erro** contendo `[HONEYPOT]` e o nome do usuário que tentou o acesso.

---

## 4. Checklist de Integração de APIs (Timeouts e Encoding)

### Timeouts (RestTemplate)
- **Automático**: **RestTemplateConfigTest** garante que o `RestTemplate` usa `SimpleClientHttpRequestFactory` com timeouts configurados (10s connect, 10s read).  
- **Manual**: fontes lentas (ex.: Exploit-DB) não devem derrubar o bot; após 10s de leitura a chamada falha e o erro é logado (ex.: `⚠️ [RSS-FAIL] ...` ou `⚠️ [CISA] Erro: ...`), e o ciclo continua.

### Encoding (caracteres especiais)
- **CERT.br e ANPD** (e outros RSS em português): verifique no Discord se caracteres como **ç, á, ã, ó** e símbolos aparecem corretamente nas mensagens de alerta.
- **Se houver problema**: confira se o RSS está em UTF-8 e se o cliente HTTP/RSS (Rome `XmlReader`, etc.) está usando UTF-8. O Jackson para JSON já usa UTF-8 por padrão.

---

## Resumo dos testes automatizados

| Teste | Classe | O que valida |
|-------|--------|----------------|
| Idempotência | `VulnerabilityServiceTest` | Não duplica registro; JSON íntegro |
| Concorrência na persistência | `VulnerabilityServiceTest` | Várias threads escrevendo sem corromper |
| Honeypot owner | `CommandListenerAdminPanelTest` | Owner vê mensagem de boas-vindas |
| Honeypot não-owner | `CommandListenerAdminPanelTest` | "Acesso Negado" + log no console |
| Timeouts RestTemplate | `RestTemplateConfigTest` | Factory com timeouts 10s |
| Concorrência /feeds vs ciclo | `ThreatIntelligenceConcurrencyTest` | getSources() não trava durante ciclo |
| Contexto Spring | `ProjetoCybersegurancaBotJavaApplicationTests` | Contexto sobe com perfil `test` |

Para rodar todos os testes:

```bash
./gradlew test
```
