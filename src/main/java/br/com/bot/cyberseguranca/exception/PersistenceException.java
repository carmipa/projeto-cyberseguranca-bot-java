package br.com.bot.cyberseguranca.exception;

public class PersistenceException extends BotBaseException {
    public PersistenceException(String mensagem, Throwable causa) {
        super("ERRO_JSON: " + mensagem, causa);
    }
}