package br.com.bot.cyberseguranca.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiSource(
    String name,
    String url,
    @JsonProperty("auth_type") String authType,
    String description
) {}