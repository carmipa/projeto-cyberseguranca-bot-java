package br.com.bot.cyberseguranca.exception;

public class ConfigurationException extends BotBaseException {
    public ConfigurationException(String mensagem) {
        super("FALHA_CONFIG: " + mensagem);
    }
}