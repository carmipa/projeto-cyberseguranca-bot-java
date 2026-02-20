package br.com.bot.cyberseguranca.exception;

public abstract class BotBaseException extends RuntimeException {

    public BotBaseException(String message) {
        super(message);
    }

    public BotBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
