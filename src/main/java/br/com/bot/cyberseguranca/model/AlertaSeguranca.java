package br.com.bot.cyberseguranca.model;

import java.time.LocalDateTime;

public record AlertaSeguranca(
    String id,
    String titulo,
    String severidade,
    String status,
    LocalDateTime dataIdentificacao,
    String descricao
) {}