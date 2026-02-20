package br.com.bot.cyberseguranca.exception;

public class GrcValidationException extends BotBaseException {
    public GrcValidationException(String mensagem) {
        super("VIOLACAO_GRC: " + mensagem);
    }
}