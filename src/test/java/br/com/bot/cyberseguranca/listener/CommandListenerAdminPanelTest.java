package br.com.bot.cyberseguranca.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 3. Validação do "Malandro Protocol" (Honeypot).
 * OWNER_ID libera acesso; outras contas recebem "Acesso Negado" e o log de erro no console.
 */
@ExtendWith(MockitoExtension.class)
class CommandListenerAdminPanelTest {

    private static final String OWNER_ID = "owner-carminati-123";

    @Mock
    private SlashCommandInteractionEvent event;
    @Mock
    private User user;
    @Mock
    private ReplyCallbackAction replyAction;
    @Mock
    private InteractionHook hook;

    private CommandListener listener;
    private ByteArrayOutputStream stderrCapture;

    @BeforeEach
    void setUp() {
        br.com.bot.cyberseguranca.service.VulnerabilityService vs = null;
        br.com.bot.cyberseguranca.service.ThreatIntelligenceService ts = null;
        br.com.bot.cyberseguranca.service.BotConfigService cs = null;
        listener = new CommandListener(vs, ts, cs, OWNER_ID);
        stderrCapture = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("admin_panel com OWNER_ID: libera acesso e exibe mensagem de boas-vindas")
    void admin_panel_owner_libera_acesso() {
        when(event.getName()).thenReturn("admin_panel");
        when(event.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(OWNER_ID);
        when(event.reply(anyString())).thenReturn(replyAction);
        when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);

        listener.onSlashCommandInteraction(event);

        ArgumentCaptor<String> replyCaptor = ArgumentCaptor.forClass(String.class);
        verify(event).reply(replyCaptor.capture());
        assertThat(replyCaptor.getValue()).contains("Bem-vindo", "Carminati", "Painel administrativo liberado");
    }

    @Test
    @DisplayName("admin_panel com outra conta: Acesso Negado e log de honeypot no console")
    void admin_panel_nao_owner_acesso_negado_e_log_honeypot() {
        System.setErr(new PrintStream(stderrCapture));
        when(event.getName()).thenReturn("admin_panel");
        when(event.getUser()).thenReturn(user);
        when(user.getId()).thenReturn("outro-usuario-456");
        when(user.getName()).thenReturn("Atacante");
        when(event.reply(anyString())).thenReturn(replyAction);
        when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);

        listener.onSlashCommandInteraction(event);

        ArgumentCaptor<String> replyCaptor = ArgumentCaptor.forClass(String.class);
        verify(event).reply(replyCaptor.capture());
        assertThat(replyCaptor.getValue()).contains("Acesso Negado", "trilha de auditoria");

        String stderr = stderrCapture.toString();
        assertThat(stderr).contains("HONEYPOT", "não autorizada", "Admin Panel");
        System.setErr(System.err);
    }
}
