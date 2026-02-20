package br.com.bot.cyberseguranca.model;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record AlertaSeguranca(
        @NotBlank String id,
        @NotBlank String sistema,
        String criticidade, // CRITICAL, HIGH, MEDIUM
        String descricao,
        LocalDateTime dataDetectada
){}
